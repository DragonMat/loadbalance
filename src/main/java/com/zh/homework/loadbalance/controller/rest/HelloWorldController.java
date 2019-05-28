package com.zh.homework.loadbalance.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    /**
     * 测试所用接口
     *
     * @return
     */
    @GetMapping("/hello")
    public String helloWorld(){
        return "hello world";
    }
}
