package com.ws.benny.websocket;

import com.alibaba.fastjson.JSON;
import com.ws.benny.service.RedisTopicSendMessageService;
import com.ws.benny.websocket.auth.MyEndpointConfigurator;
import com.ws.benny.websocket.domain.MessageTypeEnum;
import com.ws.benny.websocket.domain.SocketMessageDomain;
import com.ws.benny.websocket.domain.SystemMessageDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangbinbin
 * @create 2023/2/27 10:48
 */
@Slf4j
@Component
@ServerEndpoint(value = "/ws", configurator = MyEndpointConfigurator.class)
public class WebSocketServer {

    /**
     * 用来解决webSocket中无法注入mapper
     */
    private static RedisTopicSendMessageService redisTopicSendMessageService;

    @Autowired
    public void setRedisTopicSendMessageService(RedisTopicSendMessageService redisTopicSendMessageService) {
        WebSocketServer.redisTopicSendMessageService = redisTopicSendMessageService;
    }

    /**
     * 记录当前在线连接数
     * 考虑用户缓存 不使用CopyOnWriteArrayList,使用Map保存
     * public static final CopyOnWriteArrayList<WebSocketServer> webSockets = new CopyOnWriteArrayList<>();
     */
    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        String userId = getUserId(session);
        try {
            sessionMap.put(userId, session);
            //保存上线状态 构建用户信息发送消息给其他客户端 TODO处理异常catch兜底
            redisTopicSendMessageService.notifyUserStatusChange(userId, MessageTypeEnum.ONLINE);
            log.info("【WebSocketServer】【onOpen】客户端连接，userId={}", userId);
        } catch (Exception e) {
            log.error("【WebSocketServer】【onOpen】客户端连接异常, e: {}", e.getMessage());
            if (sessionMap.containsKey(userId))
                sessionMap.remove(userId);
        }
        //发送消息给自己 消息内容为在线工程师列表 <需要保存登录时间,上线不等于拉取好友列表,修改为上线不发送>
//        new Thread(() -> {
//            sendInfo(session);
//        }).start();
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        String userId = getUserId(session);
        try {
            redisTopicSendMessageService.notifyUserStatusChange(userId, MessageTypeEnum.OFFLINE);
            sessionMap.remove(userId);
            log.info("【WebSocketServer】【onClose】客户端关闭，userId={}", userId);
        } catch (Exception e) {
            log.info("【WebSocketServer】【onClose】客户端关闭异常，e: {}", e.getMessage());
            sessionMap.remove(userId);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * 后台收到客户端发送过来的消息
     * onMessage 是一个消息的中转站
     * 接受 浏览器端 socket.send 发送过来的 json数据
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            String userId = getUserId(session);
            log.info("【WebSocketServer】【onMessage】服务端收到用户userId={}的消息:{}", userId, JSON.toJSONString(message));
            SystemMessageDomain messageDomain = JSON.parseObject(message, SystemMessageDomain.class);
            if (MessageTypeEnum.PING.equals(messageDomain.getType())) {
                //如果是ping消息 直接发送pong
                sendMessage(session, SocketMessageDomain.createPongMessage());
                //续期登录状态
                redisTopicSendMessageService.heartbeat(userId);
            } else {
                //如果是其他消息 执行发布策略
                messageDomain.setSender(userId);
                redisTopicSendMessageService.handleMessage(userId, messageDomain);
            }
        } catch (Exception e) {
            log.info("【WebSocketServer】【onMessage】消息处理异常,e: {}", e.getMessage());
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        String userId = getUserId(session);
        log.error("【WebSocketServer】【onError】出现异常，e: {}", error.getMessage());
        error.printStackTrace();
        if (session.isOpen()) {
            // 如果连接已经打开，尝试重新连接 TODO 要不要调用onOpen 因为onOpen会给其他用户发送消息
            onOpen(session);
            log.info("【WebSocketServer】【onError】重新连接,userId: {}", userId);
        } else {
            // 否则关闭连接
            log.info("【WebSocketServer】【onError】关闭连接userId: {}", userId);
            onClose(session);
        }
    }

    @PreDestroy
    public void onDestroy() {
        log.info("【WebSocketServer】【onDestroy】容器即将销毁,执行清除策略......");
        //删除登录信息
        redisTopicSendMessageService.onDestroy(sessionMap);
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(Session session, String message) {
        try {
            log.info("服务端给用户userId{}发送消息{}", getUserId(session), JSON.toJSONString(message));
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

    /**
     * 向userId发送消息
     */
    public void sendMessageToUser(String userId, SocketMessageDomain message) throws IOException {
        try {
            log.info("发送消息到:" + userId + "，报文:" + message);
            sessionMap.get(userId).getBasicRemote().sendText(JSON.toJSONString(message));
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

    /**
     * 从session获取当前userId
     *
     * @param session
     * @return
     */
    private String getUserId(Session session) {
        return String.valueOf(session.getUserProperties().get("userId"));
    }

}
