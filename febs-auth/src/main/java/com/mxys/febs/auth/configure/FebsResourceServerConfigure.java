package com.mxys.febs.auth.configure;

import com.mxys.febs.common.handler.FebsAccessDeniedHandler;
import com.mxys.febs.common.handler.FebsAuthExceptionEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * FebsResourceServerConfigurer继承了ResourceServerConfigurerAdapter，并重写了configure(HttpSecurity http)方法，
 * 通过requestMatchers().antMatchers("/**")的配置表明该安全配置对所有请求都生效。
 * 类上的@EnableResourceServer用于开启资源服务器相关配置。
 */
@Configuration
@EnableResourceServer
public class FebsResourceServerConfigure  extends ResourceServerConfigurerAdapter {

    @Autowired
    private FebsAuthExceptionEntryPoint febsAuthExceptionEntryPoint;

    @Autowired
    private FebsAccessDeniedHandler febsAccessDeniedHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers("/**").authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.authenticationEntryPoint(febsAuthExceptionEntryPoint).accessDeniedHandler(febsAccessDeniedHandler);
    }
}
