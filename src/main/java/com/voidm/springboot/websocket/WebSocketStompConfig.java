package com.voidm.springboot.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket-STOMP 核心配置
 *
 * @author voidm
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private MyHttpSessionHandshakeInterceptor myHttpSessionHandshakeInterceptor;

    @Autowired
    private MyChannelInterceptorAdapter myChannelInterceptorAdapter;

    /**
     * 注册stomp的端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // WebSocket 协议
        registry.addEndpoint("/webSocketServer").addInterceptors(myHttpSessionHandshakeInterceptor).setAllowedOrigins("*").withSockJS();
        // registry.addEndpoint("/socketServer").addInterceptors(interceptor).setAllowedOrigins("*");
        // registry.addEndpoint("/socketServer").setAllowedOrigins("*");
    }

    /**
     * 配置信息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 订阅名称,topic 一对多,user 一对一
        registry.enableSimpleBroker("/topic", "/user");
        // 全局使用的消息前缀（客户端订阅路径上会体现出来）
        // registry.setApplicationDestinationPrefixes("/prefixes");
        // 一对一前缀，不设置的话，默认也是/user/
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(myChannelInterceptorAdapter);
    }
}