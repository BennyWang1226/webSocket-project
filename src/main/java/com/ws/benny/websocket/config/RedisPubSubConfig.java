package com.ws.benny.websocket.config;

import com.ws.benny.websocket.listener.RedisPubSubListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import static com.ws.benny.constant.ExpertManageConstant.WEBSOCKET.REDIS_TOPIC;

/**
 * 注册redis发布订阅监听器 绑定监听的topic
 *
 * @author wangbinbin
 * @create 2023/2/28 11:04
 */
@Configuration
public class RedisPubSubConfig {

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisPubSubListener());
    }

    /**
     * 配置redis消息监听器 绑定监听的topic
     *
     * @param factory
     * @return
     */
    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        /**
         * 支持多个topic
         *         String[] topisArr = {"online", "offline"};
         *         List<PatternTopic> topicList = new ArrayList<>();
         *         Arrays.stream(topisArr).forEach(c -> {
         *             topicList.add(new PatternTopic(c));
         *         });
         *         container.addMessageListener(messageListener(), topicList);
         */
        container.addMessageListener(messageListener(), new ChannelTopic(REDIS_TOPIC));
        return container;
    }

}
