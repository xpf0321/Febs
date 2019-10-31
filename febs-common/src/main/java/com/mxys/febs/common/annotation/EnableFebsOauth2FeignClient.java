package com.mxys.febs.common.annotation;

import com.mxys.febs.common.configure.FebsAuthExceptionConfigure;
import com.mxys.febs.common.configure.FebsOAuth2FeignConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FebsOAuth2FeignConfigure.class)//使用@Import将FebsOAuth2FeignConfigure配置类引入了进来。
public @interface EnableFebsOauth2FeignClient {
}
