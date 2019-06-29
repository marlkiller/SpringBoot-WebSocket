package com.voidm.springboot.websocket.interceptor;

import com.voidm.springboot.api.constant.Constants;
import com.voidm.springboot.websocket.entity.WebSocketUserAuthentication;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

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

            // 2、判断用户名和密码
            WebSocketUserAuthentication webSocketUserAuthentication = (WebSocketUserAuthentication) accessor.getUser();
            String username = accessor.getNativeHeader("username").get(0);
            String password = accessor.getNativeHeader("password").get(0);

            if (webSocketUserAuthentication != null && "admin".equals(password) && webSocketUserAuthentication.getSessionId() != null) {
                webSocketUserAuthentication.setName(username);
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

    /**
     * 1. 在消息发送完成后调用，而不管消息发送是否产生异常，在次方法中，我们可以做一些资源释放清理的工作
     * 2. 此方法的触发必须是 preSend 方法执行成功，且返回值不为null,发生了实际的消息推送，才会触发
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel messageChannel, boolean b, Exception e) {

    }

    /**
     * 1. 在消息被实际检索之前调用，如果返回 false ,则不会对检索任何消息，只适用于 (PollableChannels)，
     * 2. 在 websocket 的场景中用不到
     */
    @Override
    public boolean preReceive(MessageChannel messageChannel) {
        return true;
    }

    /**
     * 1. 在检索到消息之后，返回调用方之前调用，可以进行信息修改，如果返回 null ,就不会进行下一步操作
     * 2. 适用于 PollableChannels ，轮询场景
     */
    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel messageChannel) {
        return message;
    }

    /**
     * 1. 在消息接收完成之后调用，不管发生什么异常，可以用于消息发送后的资源清理
     * 2. 只有当 preReceive 执行成功，并返回 true 才会调用此方法
     * 2. 适用于 PollableChannels ，轮询场景
     */
    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel messageChannel, Exception e) {

    }
}