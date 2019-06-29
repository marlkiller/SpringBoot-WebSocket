package com.voidm.springboot.websocket.entity;

/**
 * @author voidm
 * @date 2019-06-29
 */
public enum WebSocketEventType {
    /**
     * 系统推送
     */
    NOTICE,
    /**
     * 单一单发送消息
     */
    PRIVATE_MSG,
    /**
     * 建立连接,初始化
     */
    INIT,
    /**
     * 新创建用户
     */
    NEW_USER
}