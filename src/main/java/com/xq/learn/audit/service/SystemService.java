package com.xq.learn.audit.service;

/**
 * 系统相关信息获取接口
 * @author easonlzhang
 */
public interface SystemService {
    /**
     * 获取用户操作, 通过请求地址获取用户操作资源描述
     * @param url request url
     * @return operation
     */
    default String operation(String url) {
        return "-";
    }

    /**
     * 获取系统名称, 通过请求地址获取系统模块名称
     * @param url request url
     * @return module
     */
    default String module(String url) {
        return  "-";
    }
}
