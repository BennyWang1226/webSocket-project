package com.ws.benny.websocket.util;

import com.alibaba.fastjson.JSON;
import com.ws.benny.websocket.domain.SocketMessageDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author wangbinbin
 * @create 2023/2/28 11:01
 */
@Component
public class RedisPubSubUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送消息
     */
    public void sendMsg(String topic, SocketMessageDomain message) {
        stringRedisTemplate.convertAndSend(topic, JSON.toJSONString(message));
    }

}
