package com.mxys.febs.server.test.service;

import com.mxys.febs.common.entity.FebsServerConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(value = FebsServerConstant.FEBS_SERVER_SYSTEM,url = "http://127.0.0.1:8201", contextId = "helloServiceClient", fallbackFactory = HelloServiceFallback.class)
public interface IHelloService {

    @GetMapping("hello")
    String hello(@RequestParam("name") String name);
}
