package com.xq.learn.audit.autoconfigure;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @see org.springframework.context.annotation.ImportSelector
 * audit ImportSelector
 * @author easonlzhang
 */
public class AuditImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {"com.xq.learn.audit.client.AuditClient", "com.xq.learn.audit.interceptor.AuditInterceptor"};
    }
}
