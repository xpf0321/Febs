package com.mxys.febs.auth.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * FebsResourceServerConfigurer继承了ResourceServerConfigurerAdapter，并重写了configure(HttpSecurity http)方法，
 * 通过requestMatchers().antMatchers("/**")的配置表明该安全配置对所有请求都生效。
 * 类上的@EnableResourceServer用于开启资源服务器相关配置。
 */
@Configuration
@EnableResourceServer
public class FebsResourceServerConfigure  extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers("/**").authenticated();
    }
}
