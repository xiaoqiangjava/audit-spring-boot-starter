package com.xq.learn.audit.autoconfigure;

import com.xq.learn.audit.annotation.Audit;
import com.xq.learn.audit.interceptor.AuditInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

/**
 * 审计日志自动装配，支持三种方式的拦截器：通过注解匹配、通过Aspect表达式匹配或者通过正则表达式匹配的方式来拦截匹配到的方法
 * @author easonlzhang
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({AuditProperties.class})
@Import(AuditImportSelector.class)
@Slf4j
public class AuditAutoConfigure {
    private static final String expressionPattern = "(execution(public * %s))";

    private final AuditProperties auditProperties;

    private AuditInterceptor auditInterceptor;

    public AuditAutoConfigure(AuditProperties auditProperties, ObjectProvider<AuditInterceptor> auditProvider) {
        this.auditProperties = auditProperties;
        this.auditInterceptor = auditProvider.getIfAvailable();
    }

    /**
     * 使用正则表达式形式来匹配需要拦截的方法
     * @return {@link DefaultPointcutAdvisor}
     */
    @Bean
    @ConditionalOnProperty(prefix = "audit", name = "expression-type", havingValue = "REGEXP")
    public DefaultPointcutAdvisor regexpAdvisor() {
        log.info("Init regexp advisor...");
        JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
        if (!ObjectUtils.isEmpty(auditProperties.getIncludePatterns())) {
            pointcut.setPatterns(auditProperties.getIncludePatterns());
        }
        if (!ObjectUtils.isEmpty(auditProperties.getExcludePatterns())) {
            pointcut.setExcludedPatterns(auditProperties.getExcludePatterns());
        }
        log.info("Finished init regexp advisor.");

        return new DefaultPointcutAdvisor(pointcut, this.auditInterceptor);
    }

    /**
     * 使用Aspect表达式来匹配需要拦截的方法, 默认采用该种形式的拦截器进行方法的拦截审计
     * @return {@link DefaultPointcutAdvisor}
     */
    @Bean
    @ConditionalOnProperty(prefix = "audit", name = "expression-type", havingValue = "ASPECT", matchIfMissing = true)
    public DefaultPointcutAdvisor aspectAdvisor() {
        log.info("Init aspect advisor...");
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        pointcut.setExpression(this.getAspectExpression());
        log.info("Finished init aspect advisor.");

        return new DefaultPointcutAdvisor(pointcut, this.auditInterceptor);
    }

    /**
     * 使用注解形式来匹配，添加了{@link Audit}注解的方法都会被拦截审计
     * @return {@link DefaultPointcutAdvisor}
     */
    @Bean
    public DefaultPointcutAdvisor annotationAdvisor() {
        AnnotationMatchingPointcut pointcut = AnnotationMatchingPointcut.forMethodAnnotation(Audit.class);
        return new DefaultPointcutAdvisor(pointcut, this.auditInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        log.info("Init rest template.");
        return new RestTemplate();
    }

    /*
     * 根据配置文件拼接Aspect表达式
     */
    private String getAspectExpression() {
        String[] includeExpressions = auditProperties.getIncludeExpressions();
        String[] excludeExpressions = auditProperties.getExcludeExpressions();
        StringBuilder includeBuilder = new StringBuilder("(");
        for (String expression : includeExpressions) {
            includeBuilder.append(String.format(expressionPattern, expression)).append(" or ");
        }
        includeBuilder.delete(includeBuilder.length() - 4, includeBuilder.length());
        includeBuilder.append(")");
        StringBuilder excludeBuilder = new StringBuilder("!(");
        for (String expression : excludeExpressions) {
            excludeBuilder.append(String.format(expressionPattern, expression)).append(" or ");
        }
        if (!ObjectUtils.isEmpty(excludeExpressions)) {
            excludeBuilder.delete(excludeBuilder.length() - 4, excludeBuilder.length());
            excludeBuilder.append(")");
            String expression = includeBuilder.append(" and ").append(excludeBuilder).toString();
            log.info("Aspect expression: {}", expression);
            return expression;
        }
        log.info("Aspect expression: {}", includeBuilder.toString());

        return includeBuilder.toString();
    }

}
