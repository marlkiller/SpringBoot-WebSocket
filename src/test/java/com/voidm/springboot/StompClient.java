package com.voidm.springboot;

import com.alibaba.fastjson.JSONObject;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

/**
 * WebSocket STOMP Client
 *
 * @author dennis
 */
public class StompClient {

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        WebSocketTransport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        SockJsClient sockJsClient = new SockJsClient(Collections.singletonList(webSocketTransport));
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        StompHeaders headers = new StompHeaders();
        headers.add("username", "aaaaaa");
        headers.add("password", "admin");
        ListenableFuture<StompSession> connect = stompClient.connect("ws://localhost:8080/web-websocket/webSocketServer", new WebSocketHttpHeaders(), headers, sessionHandler);
        try {
            StompSession stompSession = connect.get();

            // 订阅
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

            stompSession.subscribe("/user/" + "aaaaaa"+ "/queue/getResponse", new StompFrameHandler() {
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

            // send 发送
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "123123");
            stompSession.send("/sendTopic",jsonObject.toJSONString().getBytes());
            stompSession.send("/sendUser",jsonObject.toJSONString().getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        latch.await();


    }

    static class MyStompSessionHandler extends StompSessionHandlerAdapter {

        public MyStompSessionHandler() {

        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("StompHeaders: " + connectedHeaders.toString());

            session.subscribe("/topic/getResponse", new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return String.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    System.out.println("handleFrame==>" + payload.toString());
                }
            });
            session.send("/sendTopic", "{'name':'aaaaaa'}");
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            System.out.println("handleException===>" + exception.getMessage());
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            System.out.println("handleTransportError===>" + exception.getMessage());
        }
    }
}
