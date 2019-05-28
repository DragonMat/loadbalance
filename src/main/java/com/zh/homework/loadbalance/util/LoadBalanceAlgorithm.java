package com.zh.homework.loadbalance.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public abstract class LoadBalanceAlgorithm {

    /**
     * ip列表，<地址,权重>
     */
    public static Map<String,Integer> ipList = new HashMap<>();


    /**
     * 存入IP地址,无权重配置,默认权重都为0
     * @param ips
     */
    public void saveServiceIpsWithoutWeight(List<String> ips){
        ips.forEach(ip->ipList.put(ip,0));
    }

    /**
     * 有权重都IP地址
     *
     * @param ipsWhitWeight
     */
    public void saveServiceIpsWithWeight(Map<String,Integer> ipsWhitWeight){
        ipsWhitWeight.forEach((k,v)->ipList.put(k,v));
    }

    /**
     * 移除服务器地址
     *
     * @param ip
     */
    public void removeIp(String ip){
        ipList.remove(ip);
    }

    /**
     * 移除所有服务器地址
     */
    public void removeAllIps(){
        ipList.clear();
    }
    /**
     * 选择服务器IP
     *
     *@param invocation
     *
     * @return
     */
    public abstract String choseServiceIp(String invocation);
}
