package com.ws.benny.service;

import com.ws.benny.websocket.domain.MessageTypeEnum;
import com.ws.benny.websocket.domain.SystemMessageDomain;

import javax.websocket.Session;
import java.util.Map;

/**
 * @author wangbinbin
 * @create 2023/3/8 9:40
 */
public interface RedisTopicSendMessageService {

    /**
     * 处理上下线逻辑 设置用户登录状态 构建用户信息message 用以发送给其他用户
     *
     * @param userId
     * @param typeEnum
     */
    void notifyUserStatusChange(String userId, MessageTypeEnum typeEnum);

    /**
     * 处理发送消息的逻辑
     *
     * @param userId
     */
    void handleMessage(String userId, SystemMessageDomain systemMessageDomain);

    /**
     * 处理施工报告生成的逻辑
     */
    void handleReportMessage(SystemMessageDomain systemMessageDomain);

    /**
     * 心跳
     *
     * @param userId
     */
    void heartbeat(String userId);

    /**
     * 处理error的逻辑
     *
     * @param userId
     */
    void handleError(String userId);

    /**
     * 容器销毁之前的处理 删除本机在线用户标识
     *
     * @param sessionMap
     */
    void onDestroy(Map<String, Session> sessionMap);

}
