package com.xq.learn.audit.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 审计日志相关配置类
 * @author easonlzhang
 */
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {
    /**
     * 拦截器表达式类型，支持Aspect expression以及Regexp两种类型的表达式形式，默认开启Aspect拦截
     */
    private ExpressionType expressionType = ExpressionType.ASPECT;

    /**
     * 配置{@link #expressionType}为{@link ExpressionType#REGEXP} 类型的表达式时，提供需要拦截的正则表达式，
     * 默认拦截controller包下面的所有方法
     */
    private String[] includePatterns = {"com.xq.learn.*.controller.*"};

    /**
     * 配置{@link #expressionType}为{@link ExpressionType#REGEXP}类型的表达式时，配置需要排除方法的正则
     */
    private String[] excludePatterns = {};

    /**
     * 配置{@link #expressionType}为{@link ExpressionType#ASPECT}类型的表达式时，提供Aspect execution表达式
     * 的类名匹配规则，默认拦截controller包下面的所有公共方法
     */
    private String[] includeExpressions = {"com.xq.learn..*.controller..*(..)"};

    /**
     * 配置{@link #expressionType}为{@link ExpressionType#ASPECT}类型的表达式时，提供需要排除的类名匹配规则
     */
    private String[] excludeExpressions = {};

    /**
     * 配置审计日志上报的服务地址
     */
    private String serviceUrl;

    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public String[] getIncludePatterns() {
        return includePatterns;
    }

    public void setIncludePatterns(String[] includePatterns) {
        this.includePatterns = includePatterns;
    }

    public String[] getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(String[] excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    public String[] getIncludeExpressions() {
        return includeExpressions;
    }

    public void setIncludeExpressions(String[] includeExpressions) {
        this.includeExpressions = includeExpressions;
    }

    public String[] getExcludeExpressions() {
        return excludeExpressions;
    }

    public void setExcludeExpressions(String[] excludeExpressions) {
        this.excludeExpressions = excludeExpressions;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public enum ExpressionType {
        ASPECT,
        REGEXP
    }
}
