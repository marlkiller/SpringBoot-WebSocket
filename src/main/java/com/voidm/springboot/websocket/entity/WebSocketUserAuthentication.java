package com.voidm.springboot.websocket.entity;

import java.security.Principal;

/**
 * WebSocket 认证封装
 *
 * @author voidm
 */
public class WebSocketUserAuthentication implements Principal {

    /**
     * 用户名
     */
    private String name;
    /**
     * Http Session 封装
     */
    private String sessionId;

    public WebSocketUserAuthentication(String sessionId) {
        this.sessionId = sessionId;
    }

    public WebSocketUserAuthentication() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getName() {
        return name;
    }
}