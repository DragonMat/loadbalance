package com.zh.homework.loadbalance.controller.loadbalance;

import com.zh.homework.loadbalance.model.IpOptionVO;
import com.zh.homework.loadbalance.service.LoadBalanceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/load-balance")
public class LoadBalanceController {

    @Resource
    LoadBalanceService loadBalanceService;

    /**
     * 添加服务器地址
     *
     * @param ipOptionVO
     */
    @PostMapping("/server")
    public void saveServerIps(@RequestBody @Validated(IpOptionVO.AddIps.class) IpOptionVO ipOptionVO){
        loadBalanceService.addServerIps(ipOptionVO.getServerIps());
    }

    /**
     * 删除服务器地址
     *
     * @param ipOptionVO
     */
    @DeleteMapping("/server")
    public void deleteServerIp(@RequestBody @Validated(IpOptionVO.DeleteIps.class) IpOptionVO ipOptionVO){
       loadBalanceService.removeServerIp(ipOptionVO.getDeleteAllFlag(),ipOptionVO.getServerIps());
    }

}
