package com.mxys.febs.gateway.filter;

import com.mxys.febs.common.entity.FebsResponse;
import com.mxys.febs.common.utils.FebsUtil;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.protocol.RequestContent;
import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

@Slf4j
public class FebeGatewayErrorFilter extends SendErrorFilter {


    @Override
    public Object run() {
        try {
            //在该过滤器中，我们可以通过RequestContext获取到当前请求上下文，
            //通过请求上下文可以获取到当前请求的服务名称serviceId和当前请求的异常对象ExceptionHolder等信息。
            FebsResponse response = new FebsResponse();
            RequestContext ctx = RequestContext.getCurrentContext();
            String serviceId = (String) ctx.get(FilterConstants.SERVICE_ID_KEY);
            //通过异常对象我们可以继续获取到异常内容，根据不同的异常内容我们可以自定义想要的响应。
            ExceptionHolder exce = findZuulException(ctx.getThrowable());
            String errorCause = exce.getErrorCause();
            Throwable throwable = exce.getThrowable();
            String message = throwable.getMessage();
            message = StringUtils.isBlank(message) ? errorCause : message;

            response = resolveExceptionMessage(message, serviceId, response);

            HttpServletResponse httpServletResponse = ctx.getResponse();

            FebsUtil.makeResponse(httpServletResponse, MediaType.APPLICATION_ATOM_XML_VALUE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
            log.error("Zull sendError：{}", response.getMessage());
        } catch (Exception e) {
            log.error("Zull sendError：{}", e);
            ReflectionUtils.rethrowRuntimeException(e);
        }
        return null;
    }

    private FebsResponse resolveExceptionMessage(String message, String serviceId, FebsResponse febsResponse) {
        if (StringUtils.containsIgnoreCase(message, "time out")) {
            return febsResponse.message("请求" + serviceId + "服务超时");
        }
        if (StringUtils.containsIgnoreCase(message, "forwarding error")) {
            return febsResponse.message(serviceId + "服务不可用");
        }
        return febsResponse.message("Zuul请求" + serviceId + "服务异常");
    }
}
