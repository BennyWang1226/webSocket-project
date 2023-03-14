package com.ws.benny.websocket.domain;

/**
 * MessageType枚举类
 */
public enum MessageTypeEnum {
    PING, // 心跳消息
    PONG, // 心跳响应消息
    ONLINE, // 上线消息
    OFFLINE, // 下线消息
    CHAT, // 聊天消息
    REPORT, // 施工报告
    SYSTEM // 系统消息
}
