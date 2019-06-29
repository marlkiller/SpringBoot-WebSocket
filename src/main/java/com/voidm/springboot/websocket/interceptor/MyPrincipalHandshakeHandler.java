package com.voidm.springboot.websocket.interceptor;

import com.voidm.springboot.websocket.entity.WebSocketUserAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;


/**
 * 获取 HTTP Session 信息
 * 判断连接来源
 *
 * @author voidm
 */
@Component
public class MyPrincipalHandshakeHandler extends DefaultHandshakeHandler {


    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        HttpSession httpSession = getSession(request);
        if (httpSession == null) {
            return null;
        }
        // 获取登录的信息，就是controller 跳转页面存的信息，可以根据业务修改
        Object objectSession = httpSession.getAttribute("sessionId");
        if (objectSession == null || StringUtils.isEmpty(objectSession)) {
            return null;
        }
        return new WebSocketUserAuthentication((String) objectSession);
    }

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            HttpHeaders headers = serverRequest.getHeaders();
            headers.forEach((s, strings) -> {
                System.out.println(s + " : " + strings);
            });
            return serverRequest.getServletRequest().getSession(false);
        }
        return null;
    }
}
