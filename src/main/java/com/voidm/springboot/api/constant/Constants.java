package com.voidm.springboot.api.constant;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

/**
 * 全局常量
 *
 * @author voidm
 */
public class Constants {

    public static Set<String> users = new HashSet<>();

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(false);
        }
        return null;
    }
}
