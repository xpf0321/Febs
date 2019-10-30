package com.mxys.febs.common.configure;

import com.mxys.febs.common.handler.FebsAccessDeniedHandler;
import com.mxys.febs.common.handler.FebsAuthExceptionEntryPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 认证异常配置
 * @ConditionalOnMissingBean注解的意思是，当IOC容器中没有指定名称或类型的Bean的时候，就注册它
 */
public class FebsAuthExceptionConfigure {

    @Bean
    @ConditionalOnMissingBean(name = "accessDeniedHandler")
    public FebsAccessDeniedHandler febsAccessDeniedHandler(){
        return new FebsAccessDeniedHandler();
    }
    @Bean
    @ConditionalOnMissingBean(name = "authExceptionEntryPoint")
    public FebsAuthExceptionEntryPoint febsAuthExceptionEntryPoint(){
        return new FebsAuthExceptionEntryPoint();
    }

}
