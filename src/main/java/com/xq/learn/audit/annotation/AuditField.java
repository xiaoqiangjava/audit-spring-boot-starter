package com.xq.learn.audit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计字段注解，带有该注解的属性或者参数将在审计日志中进行记录，如果存在多个会使用逗号进行拼接
 * @author easonlzhang
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditField {
}
