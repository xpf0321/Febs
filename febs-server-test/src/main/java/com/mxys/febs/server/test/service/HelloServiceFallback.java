package com.mxys.febs.server.test.service;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class HelloServiceFallback implements FallbackFactory<IHelloService> {
    @Override
    public IHelloService create(Throwable throwable) {
        //使用函数式进行
        return  name->{
            log.error("调用febs-server-system服务出错", throwable);
            return "调用出错";
        };

    }
}
