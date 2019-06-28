package com.voidm.springboot.websocket;

import com.voidm.springboot.api.constant.Constants;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author voidm
 * @date 2019-06-28
 */

@Component
public class MyChannelInterceptorAdapter extends ChannelInterceptorAdapter {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //1、判断是否首次连接
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            //2、判断用户名和密码
            String username = accessor.getNativeHeader("username").get(0);
            String password = accessor.getNativeHeader("password").get(0);

            if ("admin".equals(password)) {
                Principal principal = () -> username;
                accessor.setUser(principal);
                Constants.users.add(username);
                return message;
            } else {
                HashMap<String, Object> map = new HashMap<>();
                for (Map.Entry<String, Object> entry : message.getHeaders().entrySet()) {
                    map.put(entry.getKey(), entry.getValue());
                }
                map.put("simpMessageType", SimpMessageType.OTHER);
                map.put("stompCommand", StompCommand.ERROR);
                GenericMessage<Object> msg = new GenericMessage<>(Object.class, map);
                return msg;
            }
        }
        //不是首次连接，已经登陆成功
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel messageChannel, boolean b) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // 这里只是单纯的打印，可以根据项目的实际情况做业务处理
        // 忽略心跳消息等非STOMP消息
        if (accessor.getCommand() == null) {
            return;
        }
        // 根据连接状态做处理，这里也只是打印了下，可以根据实际场景，对上线，下线，首次成功连接做处理
        switch (accessor.getCommand()) {
            // 首次连接
            case CONNECT:
                break;
            // 连接中
            case CONNECTED:
                break;
            // 下线
            case DISCONNECT:
                break;
            default:
                break;
        }
    }
}