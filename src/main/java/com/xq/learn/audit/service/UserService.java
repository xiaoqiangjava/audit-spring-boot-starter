package com.xq.learn.audit.service;

/**
 * 用户相关的接口，用来获取userId和userName信息，必须由使用者来实现
 * @author easonlzhang
 */
public interface UserService {
    /**
     * 获取用户id
     * @return userId
     */
    String userId();

    /**
     * 获取用户名称
     * @return userName
     */
    String userName();
}
