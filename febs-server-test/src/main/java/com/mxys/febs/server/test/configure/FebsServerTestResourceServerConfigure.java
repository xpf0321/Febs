package com.mxys.febs.server.test.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * 上述配置表示所有访问febs-server-test的请求都需要认证，只有通过认证服务器发放的令牌才能进行访问。
 * 然后在febs-server-test的入口类FebsServerSystemApplication上添加@EnableGlobalMethodSecurity(prePostEnabled = true)注解，表示开启Spring Cloud Security权限注解
 */
@Configuration
@EnableResourceServer
public class FebsServerTestResourceServerConfigure extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests().antMatchers("/**").authenticated();
    }
}
