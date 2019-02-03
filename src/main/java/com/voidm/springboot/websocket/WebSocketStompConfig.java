package com.voidm.springboot.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * WebSocket-STOMP 核心配置
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册stomp的端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // WebSocket 协议
        registry.addEndpoint("/webSocketServer").addInterceptors(new HttpSessionHandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
                // 绑定ip地址信息
                String ipAddress = getIpAddress(serverHttpRequest);
                map.put("ipAddress", ipAddress);
                return super.beforeHandshake(serverHttpRequest, serverHttpResponse, webSocketHandler, map);
            }

            @Override
            public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

            }
        }).setAllowedOrigins("*").withSockJS();
        // Socket 协议
        registry.addEndpoint("/socketServer").setAllowedOrigins("*");
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
        registration.interceptors(new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                //1、判断是否首次连接
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    //2、判断用户名和密码
                    String username = accessor.getNativeHeader("username").get(0);
                    String password = accessor.getNativeHeader("password").get(0);

                    if ("aaaaaa".equals(username) && "admin".equals(password)) {
                        Principal principal = new Principal() {
                            @Override
                            public String getName() {
                                return username;
                            }
                        };
                        accessor.setUser(principal);
                        return message;
                    } else {
                        return null;
                    }
                }
                //不是首次连接，已经登陆成功
                return message;
            }

        });
    }

    public static String getIpAddress(ServerHttpRequest request) {
        List<String> ips = request.getHeaders().get("X-Real-IP");
        if (null != ips) {
            return ips.get(0);
        }
        ips = request.getHeaders().get("X-Forwarded-For");
        if (null != ips) {
            int index = ips.get(0).indexOf(',');
            if (index != -1) {
                return ips.get(0).substring(0, index);
            } else {
                return ips.get(0);
            }
        } else {
            return request.getRemoteAddress().getAddress().getHostAddress();

        }
    }

}