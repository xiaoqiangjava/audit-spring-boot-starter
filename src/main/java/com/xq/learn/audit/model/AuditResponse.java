package com.xq.learn.audit.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 审计日志上报响应体
 * @author easonlzhang
 */
@Getter
@Setter
public class AuditResponse<T> {
    private int errcode;
    private String errmsg;

    private T data;
}
