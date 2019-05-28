package com.zh.homework.loadbalance.config;

import com.zh.homework.loadbalance.util.ConsistentHashAlgorithm.ConsistentHash;
import com.zh.homework.loadbalance.util.RoundRobinAlgorithm.RoundRobin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadBalanceConfig {

    @Bean(name = "consistentHash")
    ConsistentHash consistentHash(){ return new ConsistentHash();}

    @Bean(name = "roundRobin")
    RoundRobin roundRobin(){return new RoundRobin();}
}
