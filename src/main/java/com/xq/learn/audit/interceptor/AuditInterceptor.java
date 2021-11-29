package com.xq.learn.audit.interceptor;

import com.xq.learn.audit.annotation.AuditField;
import com.xq.learn.audit.annotation.AuditIgnore;
import com.xq.learn.audit.client.AuditClient;
import com.xq.learn.audit.model.AuditLog;
import com.xq.learn.audit.utils.IpUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 审计日志拦截器，拦截用户请求，记录审计信息
 * @author easonlzhang
 */
public class AuditInterceptor implements MethodInterceptor {

    private AuditClient auditClient;

    public AuditInterceptor(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 带有AuditIgnore注解的方法放行，不拦截
        AuditIgnore auditIgnore = invocation.getMethod().getAnnotation(AuditIgnore.class);
        if (null != auditIgnore) {
            return invocation.proceed();
        }
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 请求地址
        String uri = request.getServletPath();
        // 请求方法
        String method = request.getMethod();
        uri = method+":"+uri;
        String status = "正常";
        try {
            return invocation.proceed();
        } catch (Throwable throwable) {
            status = "异常";
            throw throwable;
        } finally {
            AuditLog auditLog = AuditLog.builder()
                    .id(UUID.randomUUID().toString())
                    .ip(IpUtils.getIpAddr(request))
                    .param(this.auditParam(invocation))
                    .status(status)
                    .description("访问地址: " + uri)
                    .timestamp(LocalDateTime.now())
                    .build();
            auditClient.reportAudit(auditLog, request.getServletPath());
        }
    }

    private String auditParam(MethodInvocation invocation) throws NoSuchFieldException, IllegalAccessException {
        // 获取请求参数，如果请求参数带有AuditField注解，则加入到审计日志中
        StringBuilder builder = new StringBuilder();
        Object[] arguments = invocation.getArguments();
        Parameter[] parameters = invocation.getMethod().getParameters();
        for (int i = 0; i < arguments.length; i++) {
            Parameter parameter = parameters[i];
            AuditField paramAnnotation = parameter.getAnnotation(AuditField.class);
            if (null != paramAnnotation &&
                    (this.isWrap(parameter.getType()) || arguments[i] instanceof String)) {
                builder.append(arguments[i]).append(",");
                continue;
            }

            if (!this.isWrap(parameter.getType()) && !(arguments[i] instanceof String)) {
                Field[] declaredFields = parameter.getType().getDeclaredFields();
                for (Field field : declaredFields) {
                    AuditField fieldAnnotation = field.getAnnotation(AuditField.class);
                    if (null != fieldAnnotation) {
                        Field declaredField = arguments[i].getClass().getDeclaredField(field.getName());
                        declaredField.setAccessible(true);
                        Object fieldValue = declaredField.get(arguments[i]);
                        builder.append(fieldValue).append(",");
                    }
                }
            }
        }

        return builder.length() > 0 ? builder.deleteCharAt(builder.length() - 1).toString() : null;
    }

    private boolean isWrap(Class<?> cls) {
        try {
            return ((Class<?>) cls.getDeclaredField("TYPE").get(null)).isPrimitive();
        } catch (Throwable throwable) {
            return false;
        }
    }

}

