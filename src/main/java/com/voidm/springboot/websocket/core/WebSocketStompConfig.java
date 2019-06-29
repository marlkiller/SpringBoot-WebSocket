package com.voidm.springboot.websocket.core;

import com.voidm.springboot.websocket.interceptor.MyChannelInterceptorAdapter;
import com.voidm.springboot.websocket.interceptor.MyHttpSessionHandshakeInterceptor;
import com.voidm.springboot.websocket.interceptor.MyPrincipalHandshakeHandler;
import com.voidm.springboot.websocket.interceptor.MyPrincipalHandshakeHandlerWithOutSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.List;

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

    @Autowired
    private MyPrincipalHandshakeHandler myPrincipalHandshakeHandler;

    @Autowired
    private MyPrincipalHandshakeHandlerWithOutSession myPrincipalHandshakeHandlerWithOutSession;

    /**
     * 添加这个 Endpoint，
     * 这样在网页中就可以通过 WebSocket 连接上服务,
     * 也就是我们配置 WebSocket 的服务地址,并且可以指定是否使用 SocketJS
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        /*
         * 1. 将 /webSocketServer 路径注册为STOMP的端点，
         *    用户连接了这个端点后就可以进行 websocket 通讯，支持 SocketJs
         * 2. setAllowedOrigins("*") 表示可以跨域
         * 3. withSockJS() 表示支持 SocketJS 访问
         * 4. addInterceptors 添加自定义拦截器，这个拦截器是上一个demo自己定义的获取 HttpSession 的拦截器
         * 5. setHandshakeHandler 添加拦截处理，这里 MyPrincipalHandshakeHandler 封装的认证用户信息
         */
        registry.addEndpoint("/webSocketServer")
                .addInterceptors(myHttpSessionHandshakeInterceptor).setHandshakeHandler(myPrincipalHandshakeHandler)
                .setAllowedOrigins("*").withSockJS();

        registry.addEndpoint("/socketServer")
                .addInterceptors(myHttpSessionHandshakeInterceptor).setHandshakeHandler(myPrincipalHandshakeHandlerWithOutSession)
                .setAllowedOrigins("*").withSockJS();
    }

    /**
     * 配置消息代理，哪种路径的消息会进行代理处理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 订阅名称,topic 一对多,user 一对一
        registry.enableSimpleBroker("/topic", "/user");

        /*
         * spring 内置broker对象
         * 1. 配置代理域，可以配置多个，此段代码配置代理目的地的前缀为 /topicTest 或者 /userTest
         *    我们就可以在配置的域上向客户端推送消息
         * 2，进行心跳设置，第一值表示server最小能保证发的心跳间隔毫秒数, 第二个值代码server希望client发的心跳间隔毫秒数
         * 3. 可以配置心跳线程调度器 setHeartbeatValue这个不能单独设置，不然不起作用，要配合setTaskScheduler才可以生效
         *    调度器我们可以自己写一个，也可以自己使用默认的调度器 new DefaultManagedTaskScheduler()
         */
        // registry.enableSimpleBroker("/topic", "/user").setHeartbeatValue(new long[]{20000, 20000}).setTaskScheduler(te);

        /*
         *  "/app" 为配置应用服务器的地址前缀，表示所有以/app 开头的客户端消息或请求
         *  都会路由到带有@MessageMapping 注解的方法中
         */
        // registry.setApplicationDestinationPrefixes("/app");

        /*
         *  1. 配置一对一消息前缀， 客户端接收一对一消息需要配置的前缀 如“'/user/'+userid + '/message'”，
         *     是客户端订阅一对一消息的地址 stompClient.subscribe js方法调用的地址
         *  2. 使用@SendToUser发送私信的规则不是这个参数设定，在框架内部是用UserDestinationMessageHandler处理，
         *     而不是而不是 AnnotationMethodMessageHandler 或  SimpleBrokerMessageHandler
         *     or StompBrokerRelayMessageHandler，是在@SendToUser的URL前加“user+sessionId"组成
         */
        registry.setUserDestinationPrefix("/user");

        /*
         * 自定义路径分割符
         * 注释掉的这段代码添加的分割符为. 分割是类级别的@messageMapping和方法级别的@messageMapping的路径
         * 例如类注解路径为 “topic”,方法注解路径为“hello”，那么客户端JS stompClient.send 方法调用的路径为“/app/topic.hello”
         * 注释掉此段代码后，类注解路径“/topic”,方法注解路径“/hello”,JS调用的路径为“/app/topic/hello”
         */
        //registry.setPathMatcher(new AntPathMatcher("."));
    }

    /**
     * 设置输入消息通道的线程数，默认线程为1，
     * 可以自己自定义线程数，最大线程数，线程存活时间
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        /*
         * 配置消息线程池
         * 1. corePoolSize 配置核心线程池，当线程数小于此配置时，不管线程中有无空闲的线程，都会产生新线程处理任务
         * 2. maxPoolSize 配置线程池最大数，当线程池数等于此配置时，不会产生新线程
         * 3. keepAliveSeconds 线程池维护线程所允许的空闲时间，单位秒
         */
        registration.taskExecutor().corePoolSize(10)
                .maxPoolSize(20)
                .keepAliveSeconds(60);

        /*
         * 添加stomp自定义拦截器，可以根据业务做一些处理
         * 消息拦截器，实现ChannelInterceptor接口
         */
        registration.interceptors(myChannelInterceptorAdapter);
    }

    /**
     * 设置输出消息通道的线程数，
     * 默认线程为1，可以自己自定义线程数，最大线程数，线程存活时间
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(10)
                .maxPoolSize(20)
                .keepAliveSeconds(60);
    }

    /**
     * 配置发送与接收的消息参数，
     * 可以指定消息字节大小，缓存大小，发送超时时间
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        /*
         * 1. setMessageSizeLimit 设置消息缓存的字节数大小 字节
         * 2. setSendBufferSizeLimit 设置websocket会话时，缓存的大小 字节
         * 3. setSendTimeLimit 设置消息发送会话超时时间，毫秒
         */
        registry.setMessageSizeLimit(10240)
                .setSendBufferSizeLimit(10240)
                .setSendTimeLimit(10000);
    }

    /**
     * 添加自定义的消息转换器，
     * spring 提供多种默认的消息转换器，
     * 返回 false ,不会添加消息转换器，返回 true，会添加默认的消息转换器，
     * 当然也可以把自己写的消息转换器添加到转换链中
     */
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        return true;
    }

    /**
     * 自定义控制器方法的参数类型，
     * 有兴趣可以百度 HandlerMethodArgumentResolver 这个的用法
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

    }

    /**
     * 自定义控制器方法返回值类型，
     * 有兴趣可以百度 HandlerMethodReturnValueHandler 这个的用法
     */
    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

    }
}