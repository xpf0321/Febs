package com.mxys.febs.server.test.controller;

import com.mxys.febs.server.test.service.IHelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 代码中，使用了权限注解保护方法，当用户已认证并且拥有user:add权限的时候，才能访问test1方法；
 * 当用户已认证并拥有user:update权限的时候才能访问test2方法。
 */
@RestController
public class TestController {

    @Autowired
    private IHelloService iHelloService;

    @GetMapping("hello")
    public String hello(String name){
        return iHelloService.hello(name);
    }

    @GetMapping("test1")
    @PreAuthorize("hasAnyAuthority('user:add')")
    public String test1(){
        return "拥有'user:add'权限";
    }

    @GetMapping("test2")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public String test2(){
        return "拥有'user:update'权限";
    }

    @GetMapping("user")
    public Principal currentUser(Principal principal) {
        return principal;
    }
}
