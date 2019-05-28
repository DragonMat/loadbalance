package com.zh.homework.loadbalance.util.RoundRobinAlgorithm;

import com.zh.homework.loadbalance.util.LoadBalanceAlgorithm;

import java.util.*;

public class RoundRobin extends LoadBalanceAlgorithm {

    /**
     * 轮训指针
     */
    private static Integer pos = 0;


    @Override
    public String choseServiceIp(String invocation) {

        //todo 服务器地址为空应该抛错
        if (ipList.isEmpty()){
            return "127.0.0.1";
        }

        // 重建一个Map，避免服务器的上下线导致的并发问题
        Map<String, Integer> serverMap = new HashMap<>();
        serverMap.putAll(ipList);

        // 取得Ip地址List
        Set<String> keySet = serverMap.keySet();
        ArrayList<String> keyList = new ArrayList<>();
        keyList.addAll(keySet);

        String serverIp;
        synchronized (pos) {
            if (pos >= keySet.size()) {
                pos = 0;
            }
            serverIp = keyList.get(pos);
            pos++;
        }

        return serverIp;


    }
}
