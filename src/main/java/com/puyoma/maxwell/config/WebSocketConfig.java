package com.puyoma.maxwell.config;

import com.puyoma.maxwell.log.LoggerMessage;
import com.puyoma.maxwell.log.LoggerQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.config
 * @date:2020/3/15
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * 复写了 configureMessageBroker() 方法：
     * 配置了一个 简单的消息代理，通俗一点讲就是设置消息连接请求的各种规范信息。
     * 发送应用程序的消息将会带有 “/app” 前缀。
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //定义了一个（或多个）客户端订阅地址的前缀信息，也就是客户端接收服务端发送消息的前缀信息
        registry.enableSimpleBroker("/topic");
    }

    /**
     * 推送日志到/topic/pullLogger
     */
    @PostConstruct
    public void pushLogger(){

        ExecutorService executorService= Executors.newFixedThreadPool(3);
        executorService.submit(()->{
            while (true) {
                try {
                    LoggerMessage log = LoggerQueue.getInstance().poll();
                    if(log!=null){
                        if(messagingTemplate!=null)
                            messagingTemplate.convertAndSend("/topic/pullLogger",log);
                    }
                } catch (Exception e) {
                    logger.error("",e);
                }
            }
        });
    }
}
