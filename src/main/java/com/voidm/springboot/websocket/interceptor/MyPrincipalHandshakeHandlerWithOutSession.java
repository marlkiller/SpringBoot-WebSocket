package com.voidm.springboot.websocket.interceptor;

import com.voidm.springboot.websocket.entity.WebSocketUserAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;


/**
 * 非 Web 客户端 不走 Session 验证
 * 判断连接来源
 *
 * @author voidm
 */
@Component
public class MyPrincipalHandshakeHandlerWithOutSession extends DefaultHandshakeHandler {


    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
        HttpHeaders headers = serverRequest.getHeaders();
        headers.forEach((s, strings) -> {
            System.out.println(s + " : " + strings);
        });
        return new WebSocketUserAuthentication(UUID.randomUUID().toString());
    }
}
