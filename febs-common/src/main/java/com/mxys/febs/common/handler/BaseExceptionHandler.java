package com.mxys.febs.common.handler;

import com.mxys.febs.common.entity.FebsResponse;
import com.mxys.febs.common.exception.FebsAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;

/**
 * 对于通用的异常类型捕获可以在BaseExceptionHandler中定义，
 * 而当前微服务系统独有的异常类型捕获可以在GlobalExceptionHandler中定义。
 */
@Slf4j
public class BaseExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public FebsResponse handleException(Exception e){
        log.error("系统内部异常，异常信息",e);
        return new FebsResponse().message("系统内部异常");
    }

    @ExceptionHandler(FebsAuthException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public FebsResponse handleFebsAuthException(Exception e){
        log.error("系统错误，错误信息",e);
        return new FebsResponse().message("系统错误");
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public FebsResponse handleAccessDeniedException(Exception e){
        return new FebsResponse().message("没有权限访问异常");
    }
}
