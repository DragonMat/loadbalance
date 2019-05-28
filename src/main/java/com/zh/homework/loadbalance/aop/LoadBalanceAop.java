package com.zh.homework.loadbalance.aop;

import com.zh.homework.loadbalance.service.LoadBalanceService;
import com.zh.homework.loadbalance.util.RoundRobinAlgorithm.RoundRobin;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.zh.homework.loadbalance.util.LoadBalanceAlgorithm.ipList;

@Aspect
@Component
public class LoadBalanceAop {
    private static final Logger logger = LoggerFactory.getLogger(LoadBalanceAop.class);

    @Resource
    private HttpServletRequest request;

    @Resource
    private LoadBalanceService loadBalanceService;

    @Resource
    private RoundRobin roundRobin;

    /**
     * 负载均衡
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(public * com.zh.homework.loadbalance.controller.rest..*.*(..))")
    public Object publicMethod(ProceedingJoinPoint point) throws Throwable {

        // 获取请求地址
        Object result = null;
        Long startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
        String methodName = point.getSignature().getName();
        String serverIp = loadBalanceService.choseServerIp(UUID.randomUUID().toString());

        // 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
        // 这里做转发，实际调用结果无法模拟，返回服务器地址
        try {
            //拿sessionId做为requestId
            result = serverIp;
            //转发请求
            HttpServletResponse response = null;

            //如果转发请求失败，则尝试切换服务地址进行转发（最多三次）
            if (Objects.isNull(response) || !Objects.equals(response.getStatus(), 200)) {

                List<String> errorIps = new ArrayList<>();
                errorIps.add(serverIp);
                for (int count = 1; count <= 3; count++) {
                    String ip = roundRobin.choseServiceIp(UUID.randomUUID().toString());
                    //转发请求,代码省略
                    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                    response = servletRequestAttributes.getResponse();
                    //校验结果
                    if (Objects.nonNull(response) && Objects.equals(response.getStatus(), 200)) {
                        break;
                    } else {
                        errorIps.add(ip);
                    }
                    if(Objects.equals(count,3)){
                        throw new Exception("服务器网络异常");
                    }
                }

                //对所有发生问题的服务进行ping操作，如果有问题则从服务器列表中移除
                errorIps.stream()
                        .filter(ip -> !Objects.equals(ip, "127.0.0.1"))
                        .forEach(ip -> {
                            if (isReachableTest(ip, 300)) {
                                errorIps.remove(ip);
                            } else {
                                ipList.remove(ip);
                                logger.error("{}服务器无法连接", ip);
                            }
                        });


            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        Long endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
        logger.info("方法{}被调用，花费时间:{}", methodName, (endTimeMillis - startTimeMillis));
        return result;
    }

    /**
     * 模拟PING
     * 利用InetAddress的isReachable方法可以实现ping的功能，里面参数设定超时时间，返回结果表示是否连上
     *
     * @param ip      地址
     * @param timeOut 超时时间
     */
    private Boolean isReachableTest(String ip, int timeOut) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isReachable(timeOut);//true：连接成功，false：连接失败
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
