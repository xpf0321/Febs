package com.mxys.febs.common.handler;

import com.mxys.febs.common.entity.FebsResponse;
import com.mxys.febs.common.utils.FebsUtil;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FebsAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse res, AccessDeniedException e) throws IOException, ServletException {
        FebsResponse febsResponse = new FebsResponse();
        FebsUtil.makeResponse(res, MediaType.APPLICATION_ATOM_XML_VALUE
                ,HttpServletResponse.SC_FORBIDDEN,febsResponse.message("没有权限访问该资源"));
    }
}
