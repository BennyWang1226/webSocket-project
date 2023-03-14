package com.ws.benny.websocket.listener;

import com.alibaba.fastjson.JSON;
import com.ws.benny.service.HandlerService;
import com.ws.benny.websocket.domain.SystemMessageDomain;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * redis消息监听器
 *
 * @author wangbinbin
 * @create 2023/2/28 11:03
 */
@Slf4j
@Component
@NoArgsConstructor
public class RedisPubSubListener implements MessageListener {

    private static ThreadPoolTaskExecutor executor;

    private static HandlerService handlerService;

    @Autowired
    public RedisPubSubListener(ThreadPoolTaskExecutor executor, HandlerService handlerService) {
        RedisPubSubListener.executor = executor;
        RedisPubSubListener.handlerService = handlerService;
    }

    /**
     * 消息监听
     *
     * @param message
     * @param pattern
     */
    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("监听到redis消息：{}", message);
        byte[] body = message.getBody();
        if (body.length > 0) {
            executor.execute(() -> handlerService.handler(JSON.parseObject(message.getBody(), SystemMessageDomain.class)));
        }
    }

}
