package com.zh.homework.loadbalance;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.AtomicLongMap;
import com.zh.homework.loadbalance.service.LoadBalanceService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoadbalanceApplicationTests {

    @Resource
    private LoadBalanceService loadBalanceService;

    @Test
    public void testAddIps() {
        List<String> ips = new ArrayList<>();
        ips.add("192.168.3.1");
        ips.add("192.168.3.2");
        ips.add("192.168.3.3");
        ips.add("192.168.3.4");
        ips.add("192.168.3.5");
        ips.add("192.168.3.6");
        ips.add("192.168.3.7");
        loadBalanceService.addServerIps(ips);
        Map<String, Integer> result = loadBalanceService.queryServerList();
//		System.out.println(JSONObject.toJSONString(result));
        Assert.assertEquals(result.size(), 7);
    }

    @Test
    public void testDeleteIp() {
        List<String> ips = new ArrayList<>();
        ips.add("192.168.3.1");
        ips.add("192.168.3.2");
        ips.add("192.168.3.3");
        ips.add("192.168.3.4");
        ips.add("192.168.3.5");
        ips.add("192.168.3.6");
        ips.add("192.168.3.7");
        List<String> deleteIps = new ArrayList<>();
        deleteIps.add("192.168.3.6");
        deleteIps.add("192.168.3.6");
        loadBalanceService.addServerIps(ips);
        loadBalanceService.removeServerIp(false, deleteIps);
        Map<String, Integer> result = loadBalanceService.queryServerList();
        Assert.assertEquals(result.size(), 5);
    }

    @Test
    public void testChoseServerIp() {
        List<String> ips = new ArrayList<>();
        ips.add("192.168.3.1");
        ips.add("192.168.3.2");
        ips.add("192.168.3.3");
        ips.add("192.168.3.4");
        ips.add("192.168.3.5");
        ips.add("192.168.3.6");
        ips.add("192.168.3.7");
        loadBalanceService.addServerIps(ips);


        AtomicLongMap<String> result =  AtomicLongMap.create();
        ips.forEach(ip->result.put(ip,0));

        for (int i = 0; i < 1000; i++) {
            String ip = loadBalanceService.choseServerIp(UUID.randomUUID().toString());
            result.getAndIncrement(ip);
        }

        System.out.println(JSONObject.toJSONString(result.asMap()));
    }
}
