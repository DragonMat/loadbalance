package com.zh.homework.loadbalance.util.ConsistentHashAlgorithm;

import com.zh.homework.loadbalance.util.LoadBalanceAlgorithm;
import org.springframework.stereotype.Component;

import java.util.*;

public class ConsistentHash extends LoadBalanceAlgorithm {

    private static final long FNV_32_INIT = 2166136261L;
    private static final int FNV_32_PRIME = 16777619;

    private final static int VIRTUAL_NODE_SIZE = 10;
    private final static String VIRTUAL_NODE_SUFFIX = "&&";

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

        //计算请求都hash
        int invocationHashCode = getHashCode(invocation);

        TreeMap<Integer, String> hashRing = buildConsistentHashRing(keyList);
        String serverIp = locate(hashRing, invocationHashCode);
        return serverIp;
    }


    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值
     *
     * @param origin
     * @return
     */
    public int getHashCode(String origin) {
        final int p = FNV_32_PRIME;
        int hash = (int) FNV_32_INIT;
        for (int i = 0; i < origin.length(); i++)
            hash = (hash ^ origin.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        hash = Math.abs(hash);
        return hash;
    }

    /**
     * 构造hash环
     *
     * @param ips
     * @return
     */
    private TreeMap<Integer, String> buildConsistentHashRing(List<String> ips) {
        //key表示服务器的hash值，value表示服务器
        TreeMap<Integer, String> virtualNodeRing = new TreeMap<>();
        for (String ip : ips) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                // 新增虚拟节点的方式如果有影响，也可以抽象出一个由物理节点扩展虚拟节点的类
                virtualNodeRing.put(getHashCode(ip + VIRTUAL_NODE_SUFFIX + i), ip);
            }
        }
        return virtualNodeRing;
    }

    /**
     * 获取服务器节点都ip
     *
     * @param ring
     * @param invocationHashCode
     * @return
     */
    private String locate(TreeMap<Integer, String> ring, int invocationHashCode) {
        // 向右找到第一个 key
        Map.Entry<Integer, String> locateEntry = ring.ceilingEntry(invocationHashCode);
        if (locateEntry == null) {
            // 想象成一个环，超过尾部则取第一个 key
            locateEntry = ring.firstEntry();
        }
        return locateEntry.getValue();
    }
}
