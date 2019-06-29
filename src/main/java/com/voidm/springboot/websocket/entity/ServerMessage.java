package com.voidm.springboot.websocket.entity;


import com.voidm.springboot.api.constant.Constants;

import java.util.Set;

/**
 * 服务端消息实体
 *
 * @author voidm
 * @date 2018-10-16
 */
public class ServerMessage {

    private String responseMessage;
    private WebSocketEventType type = WebSocketEventType.INIT;
    private static Set users = Constants.users;

    public ServerMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }


    public WebSocketEventType getType() {
        return type;
    }

    public void setType(WebSocketEventType type) {
        this.type = type;
    }

    public Set getUsers() {
        return users;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
