package com.mxys.febs.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.mxys.febs.common.entity.FebsResponse;
import com.mxys.febs.common.utils.FebsUtil;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FebsAuthExceptionEntryPoint implements AuthenticationEntryPoint {

    /**
     * FebsAuthExceptionEntryPoint实现了AuthenticationEntryPoint接口的commence方法，在方法内自定义了响应的格式。
     *
     * 其中application/json;charset=UTF-8和HTTP状态码401
     * @param req
     * @param res
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException, ServletException {
        FebsResponse febsResponse = new FebsResponse();
        FebsUtil.makeResponse(res,MediaType.APPLICATION_ATOM_XML_VALUE
                ,HttpServletResponse.SC_UNAUTHORIZED,febsResponse.message("token无效"));
    }
}
