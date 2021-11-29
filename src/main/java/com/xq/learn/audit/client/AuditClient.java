package com.xq.learn.audit.client;

import com.xq.learn.audit.autoconfigure.AuditProperties;
import com.xq.learn.audit.model.AuditLog;
import com.xq.learn.audit.model.AuditResponse;
import com.xq.learn.audit.service.SystemService;
import com.xq.learn.audit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/**
 * 审计日志上报客户端
 *
 * @author easonlzhang
 */
@Slf4j
public class AuditClient {
    private final RestTemplate restTemplate;

    private final AuditProperties auditProperties;

    @Autowired(required = false)
    private SystemService systemService = new SystemService() {
    };

    @Autowired
    private UserService userService;

    public AuditClient(AuditProperties auditProperties, @Lazy RestTemplate restTemplate) {
        this.auditProperties = auditProperties;
        this.restTemplate = restTemplate;
    }

    public void reportAudit(AuditLog auditLog, String servletPath) {
        log.info("Start report audit log...");
        auditLog.setUserId(userService.userId());
        auditLog.setUserName(userService.userName());
        auditLog.setOperation(systemService.operation(servletPath));
        auditLog.setModule(systemService.module(servletPath));
        log.info("Audit report url: {}", auditProperties.getServiceUrl());
        restTemplate.postForEntity(auditProperties.getServiceUrl(), auditLog, AuditResponse.class).getBody();
        AuditResponse<String> response = restTemplate.exchange(
                auditProperties.getServiceUrl(),
                HttpMethod.POST,
                new HttpEntity<>(auditLog),
                new ParameterizedTypeReference<AuditResponse<String>>() {
                }).getBody();
        if (null == response || 0 != response.getErrcode()) {
            log.error("<<<<Failed to report audit log!");
        }
    }
}
