package com.voidm.springboot.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * @author voidm
 * @date 2019-06-28
 */
@Component
public class MyHttpSessionHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        String ipAddress = getIpAddress(serverHttpRequest);
        map.put("ipAddress", ipAddress);
        return super.beforeHandshake(serverHttpRequest, serverHttpResponse, webSocketHandler, map);
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }

    private static String getIpAddress(ServerHttpRequest request) {
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