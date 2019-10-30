package com.mxys.febs.common.annotation;

import com.mxys.febs.common.configure.FebsAuthExceptionConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FebsAuthExceptionConfigure.class)//使用@Import将FebsAuthExceptionConfigure配置类引入了进来。
public @interface EnableFebsAuthExecptionHandler {
}
