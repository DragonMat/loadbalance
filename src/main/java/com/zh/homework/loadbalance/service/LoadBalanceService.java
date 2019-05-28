package com.zh.homework.loadbalance.service;

import com.alibaba.fastjson.JSONObject;
import com.zh.homework.loadbalance.aop.LoadBalanceAop;
import com.zh.homework.loadbalance.util.ConsistentHashAlgorithm.ConsistentHash;
import com.zh.homework.loadbalance.util.LoadBalanceAlgorithm;
import com.zh.homework.loadbalance.util.RoundRobinAlgorithm.RoundRobin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.zh.homework.loadbalance.util.LoadBalanceAlgorithm.ipList;


@Service
public class LoadBalanceService {

    @Autowired
    private RoundRobin roundRobin;

    @Autowired
    private ConsistentHash consistentHash;

    private static final Logger logger = LoggerFactory.getLogger(LoadBalanceAop.class);

    @Value("${load.balance.algorithm}")
    private String loadBalance;

    /**
     * 选择服务器IP
     *
     * @param invocation
     * @return
     */
    public String choseServerIp(String invocation) {
        return choseAlgorithm().choseServiceIp(invocation);
    }

    /**
     * 添加服务器地址
     *
     * @param ips
     */
    public void addServerIps(List<String> ips) {
        choseAlgorithm().saveServiceIpsWithoutWeight(ips);
    }

    /**
     * 删除服务地址
     *
     * @param deleteAll
     * @param ips
     */
    public void removeServerIp(Boolean deleteAll, List<String> ips) {
        if (deleteAll) {
            choseAlgorithm().removeAllIps();
        } else {
            ips.forEach(ip -> choseAlgorithm().removeIp(ip));
            logger.info("ipList:{}", JSONObject.toJSONString(ipList));
        }
    }

    /**
     * 查询服务器列表
     *
     * @return
     */
    public Map<String,Integer> queryServerList(){
        return ipList;
    }

    /**
     * 选择负载均衡策略，默认是轮训都方式
     *
     * @return
     */
    private LoadBalanceAlgorithm choseAlgorithm() {

        logger.info("负载均衡策略：{}", loadBalance);
        if (Objects.equals("ConsistentHash", loadBalance)) {
            return consistentHash;
        }
        return roundRobin;
    }
}
