package com.ws.benny.service.impl;

import com.alibaba.fastjson.JSON;
import com.ws.benny.cache.CacheKeyAssist;
import com.ws.benny.cache.CacheKeyEnum;
import com.ws.benny.service.RedisTopicSendMessageService;
import com.ws.benny.service.cache.CacheService;
import com.ws.benny.websocket.domain.MessageTypeEnum;
import com.ws.benny.websocket.domain.SocketMessageDomain;
import com.ws.benny.websocket.domain.SystemMessageDomain;
import com.ws.benny.websocket.util.RedisPubSubUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ws.benny.constant.ExpertManageConstant.WEBSOCKET.REDIS_TOPIC;

/**
 * @author wangbinbin
 * @create 2023/3/8 9:42
 */
@Slf4j
@Service
public class RedisTopicSendMessageServiceImpl implements RedisTopicSendMessageService {

    private static final String SYSTEM_NAME = "system";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisPubSubUtils redisPubSubUtils;

    @Autowired
    private CacheService<String> cacheService;

    @Override
    public void notifyUserStatusChange(String userId, MessageTypeEnum typeEnum) {
        // . 创建消息实体
        SocketMessageDomain message = createUserStateMessage(userId, typeEnum);
        switch (typeEnum) {
            case ONLINE:
                // 1.1 存储到redis 保存当前用户登录状态
                CacheKeyAssist<String> cacheKey = new CacheKeyAssist<>(String.class, CacheKeyEnum.ONLINE_FLAG, 15L, TimeUnit.SECONDS, userId);
                cacheService.set(cacheKey, "1");
                // 1.2 构建出当前用户的信息 准备发送给其他客户端
                message.setContent(JSON.toJSONString("此处为发送的用户信息"));
                break;
            case OFFLINE:
                // 2.1 删除用户登录状态
                stringRedisTemplate.delete(new CacheKeyAssist<>(String.class,
                        CacheKeyEnum.ONLINE_FLAG, userId).buildKey());
                message.setContent(JSON.toJSONString("此处为发送的用户信息"));
                break;
            default:
                break;
        }
        log.info("发布redis消息,topic:{},用户{}的状态变更消息,message: {}", REDIS_TOPIC, userId, JSON.toJSONString(message));
        // 3. redis发布订阅通知到其他客户端该用户状态变化
        redisPubSubUtils.sendMsg(REDIS_TOPIC, message);
    }

    @Override
    public void handleMessage(String userId, SystemMessageDomain systemMessageDomain) {
        if (MessageTypeEnum.CHAT.equals(systemMessageDomain.getType())) {
            redisPubSubUtils.sendMsg(REDIS_TOPIC, systemMessageDomain);
        }
    }

    @Override
    public void handleReportMessage(SystemMessageDomain systemMessageDomain) {
        redisPubSubUtils.sendMsg(REDIS_TOPIC, systemMessageDomain);
    }

    @Override
    public void heartbeat(String userId) {
        CacheKeyAssist<String> cacheKey = new CacheKeyAssist<>(String.class, CacheKeyEnum.ONLINE_FLAG, 15L, TimeUnit.SECONDS, userId);
        cacheService.expire(cacheKey, "1");
    }

    @Override
    public void handleError(String userId) {
        notifyUserStatusChange(userId, MessageTypeEnum.OFFLINE);
    }

    @Override
    public void onDestroy(Map<String, Session> sessionMap) {
        Iterator<Map.Entry<String, Session>> iterator = sessionMap.entrySet().iterator();
        log.info("容器销毁前的处理, 接收到map: {}", sessionMap);
        while (iterator.hasNext()) {
            Map.Entry<String, Session> next = iterator.next();
            String key = next.getKey();
            //后续对缓存清除 保存登录时长
            stringRedisTemplate.delete(new CacheKeyAssist<>(String.class,
                    CacheKeyEnum.ONLINE_FLAG, key).buildKey());
            log.info("清除userId: {}的登录信息", key);
        }
        log.info("登录标识删除成功");
    }

    /**
     * 创建message : ONLINE OFFLINE
     */
    private SocketMessageDomain createUserStateMessage(String userId, MessageTypeEnum typeEnum) {
        SystemMessageDomain onLineMessage = new SystemMessageDomain();
        onLineMessage.setType(typeEnum);
        // TODO 检测到用户上下线消息 查询在线用户列表
        List<String> list = Arrays.asList("1", "2");
        //设置需要接收的人员列表 <避免在其他客户端多次轮询浪费资源>
        onLineMessage.setReceiverList(list);
        return onLineMessage;
    }

}
