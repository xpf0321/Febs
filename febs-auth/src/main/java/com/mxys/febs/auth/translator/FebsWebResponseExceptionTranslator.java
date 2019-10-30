package com.mxys.febs.auth.translator;

import com.mxys.febs.common.entity.FebsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * 异常翻译器，将这些认证类型异常翻译为友好的格式
 * @author :mxys
 * @date 2019-10-30
 */
@Slf4j//@sl4j注解为lombok类型注解，用于往当前类中注入org.slf4j.Logger日志打印对象
@Component
public class FebsWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    /**
     * FebsWebResponseExceptionTranslator实现了WebResponseExceptionTranslator接口，用于覆盖默认的认证异常响应。
     * 在translate方法中，我们通过Exception异常对象的类型和内容将异常归类，
     * 并且统一返回500HTTP状态码（HttpStatus.INTERNAL_SERVER_ERROR）。
     * @param e
     * @return
     * @throws Exception
     */
    @Override
    public ResponseEntity translate(Exception e) throws Exception {
        ResponseEntity.BodyBuilder status=ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        FebsResponse response = new FebsResponse();
        String message="认证失败";
        log.error(message,e);
        if(e instanceof UnsupportedGrantTypeException){
            message = "不支持该认证类型";
            return status.body(response.message(message));
        }
        if(e instanceof InvalidGrantException){
            if (StringUtils.containsIgnoreCase(e.getMessage(), "Invalid refresh token")) {
                message = "refresh token无效";
                return status.body(response.message(message));
            }
            if (StringUtils.containsIgnoreCase(e.getMessage(), "locked")) {
                message = "用户已被锁定，请联系管理员";
                return status.body(response.message(message));
            }
            message = "用户名或密码错误";
            return status.body(response.message(message));
        }
        return status.body(response.message(message));
    }
}
