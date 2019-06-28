package com.voidm.springboot.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;


/**
 * 处理WebSocket订阅,消息发送
 *
 * @author voidm
 */
@Controller
public class WebSocketAction {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 服务端接收一对多响应
     */
    @MessageMapping("/sendTopic")
    @SendTo("/topic/getResponse")
    public ServerMessage sendTopic(ClientMessage message, StompHeaderAccessor stompHeaderAccessor,Principal principal) {
        logger.info("接收到了信息" + message.getName());
        return new ServerMessage("一对多服务 响应");
    }

    /**
     * 服务端接收一对一响应
     */
    @MessageMapping("/sendUser")
    @SendToUser("/queue/getResponse")
    public ServerMessage sendUser(ClientMessage message, StompHeaderAccessor stompHeaderAccesso,Principal principal) {
        stompHeaderAccesso.getSessionAttributes();
        logger.info("接收到了信息" + message.getName());
        return new ServerMessage("一对一服务 响应");
    }


    /**
     * 一对一订阅通知
     */
    @SubscribeMapping("user/{userId}/queue/getResponse")
    public ServerMessage subOnUser(@DestinationVariable String userId, StompHeaderAccessor stompHeaderAccessor,Principal principal) {
        logger.info(userId + "/queue/getResponse 已订阅");
        return new ServerMessage("感谢你订阅了 一对一服务");
    }

    /**
     * 一对多订阅通知
     */
    @SubscribeMapping("/topic/getResponse")
    public ServerMessage subOnTopic(Principal principal) {
        logger.info("/topic/getResponse 已订阅");
        return new ServerMessage("感谢你订阅了 一对多服务");
    }
}
