package com.voidm.springboot.websocket;


/**
 * 客户端发送消息实体
 *
 * @author voidm
 * @date 2018-10-16
 */
public class ClientMessage {
    private String toUser;
    private String text;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }
}
