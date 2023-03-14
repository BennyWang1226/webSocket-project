package com.ws.benny.websocket.handler.impl;

import com.alibaba.fastjson.JSON;
import com.ws.benny.websocket.WebSocketServer;
import com.ws.benny.websocket.domain.ChatMessageDomain;
import com.ws.benny.websocket.domain.SystemMessageDomain;
import com.ws.benny.websocket.handler.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 用户发送消息事件处理
 */
@Slf4j
@Service
public class ChatEventHandler implements EventHandler {

    private static WebSocketServer webSocketServer;

    @Autowired
    public ChatEventHandler(WebSocketServer webSocketServer) {
        ChatEventHandler.webSocketServer = webSocketServer;
    }

    /**
     * 用户给用户之间发送消息转发
     *
     * @param systemMessageDomain
     */
    @Override
    public void doHandler(SystemMessageDomain systemMessageDomain) throws IOException {
        log.info("收到用户发送消息,message: {}", JSON.toJSONString(systemMessageDomain));
        //向receiverList发送消息
        List<String> receiverList = systemMessageDomain.getReceiverList();
        for (String userId : receiverList) {
            //如果用户在此机器上登录 就发消息
            if (WebSocketServer.sessionMap.containsKey(userId)) {
                //发送的时候需要携带发送人信息
                ChatMessageDomain message = new ChatMessageDomain();
                BeanUtils.copyProperties(systemMessageDomain, message);
                webSocketServer.sendMessageToUser(userId, message);
            }
        }
    }

}
