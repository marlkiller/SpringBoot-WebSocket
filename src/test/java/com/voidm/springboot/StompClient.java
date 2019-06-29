package com.voidm.springboot;

import com.alibaba.fastjson.JSONObject;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

/**
 * WebSocket STOMP Client
 *
 * @author dennis
 */
public class StompClient {

    private static ListenableFuture<StompSession> connect;
    private static String userId;

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        webSocketContainer.setDefaultMaxTextMessageBufferSize(50 * 1024 * 1024);
        webSocketContainer.setDefaultMaxBinaryMessageBufferSize(50 * 1024 * 1024);
        WebSocketTransport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient(webSocketContainer));
        SockJsClient sockJsClient = new SockJsClient(Collections.singletonList(webSocketTransport));
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        StompHeaders headers = new StompHeaders();
        userId = "user_" + System.currentTimeMillis();
        headers.add("username", userId);
        headers.add("password", "admin");


        // http://127.0.0.1:8080/web-websocket/webSocketServer
        // connect = stompClient.connect("wss://8080-bbaa5560-54f1-47bb-93ac-fcda1db17a03.ws-ap0.gitpod.io/web-websocket/webSocketServer", new WebSocketHttpHeaders(), headers, sessionHandler);
        connect = stompClient.connect("ws://127.0.0.1:8080/web-websocket/socketServer", new WebSocketHttpHeaders(), headers, sessionHandler);
        latch.await();
    }

    static class MyStompSessionHandler extends StompSessionHandlerAdapter {

        public MyStompSessionHandler() {

        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("StompHeaders: " + connectedHeaders.toString());

            StompSession stompSession = null;
            try {
                stompSession = connect.get();
                // 订阅
                stompSession.subscribe("/user/" + userId + "/queue/getResponse", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        return Object.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders stompHeaders, Object o) {
                        String msg = new String((byte[]) o);
                        System.out.println("msg = " + msg);
                    }
                });

                stompSession.subscribe("/topic/getResponse", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        return Object.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders stompHeaders, Object o) {
                        String msg = new String((byte[]) o);
                        System.out.println("msg = " + msg);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            // send 发送
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", "this is msg " + " > one");
            stompSession.send("/sendTopic", jsonObject.toJSONString().getBytes());
            jsonObject.put("text", "this is msg " + " > more");
            stompSession.send("/sendUser", jsonObject.toJSONString().getBytes());
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            try {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            try {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
