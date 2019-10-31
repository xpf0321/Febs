package com.mxys.febs.common.configure;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


public class FebsOAuth2FeignConfigure  {

   /* *
    代码中，我们通过SecurityContextHolder从请求上下文中获取了OAuth2AuthenticationDetails类型对象，
    并通过该对象获取到了请求令牌，然后在请求模板对象requestTemplate的头部手动将令牌添加上去。*/

    @Bean
    public RequestInterceptor oauth2FeginRequestInterceptor(){
        return  requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            Object detailes= SecurityContextHolder.getContext().getAuthentication().getDetails();

            /*Object detailes= SecurityContextHolder.getContext().getAuthentication().getDetails();
            if(detailes instanceof OAuth2AuthenticationDetails){
                String authToken=((OAuth2AuthenticationDetails) detailes).getTokenValue();
                requestTemplate.header(HttpHeaders.AUTHORIZATION,"banner "+authToken);
            }*/
        };
    }

    /*@Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object detailes= SecurityContextHolder.getContext().getAuthentication().getDetails();

        //requestTemplate.header(HttpHeaders.AUTHORIZATION,"banner "+authToken);

    }*/
}
