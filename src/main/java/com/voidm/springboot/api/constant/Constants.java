package com.voidm.springboot.api.constant;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;

import javax.servlet.http.HttpSession;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局常量
 *
 * @author voidm
 */
public class Constants {

    public static Set<String> users = ConcurrentHashMap.newKeySet();

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(false);
        }
        return null;
    }
}
