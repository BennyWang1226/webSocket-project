package com.ws.benny.websocket.handler.impl;

import com.alibaba.fastjson.JSON;
import com.ws.benny.websocket.WebSocketServer;
import com.ws.benny.websocket.domain.SocketMessageDomain;
import com.ws.benny.websocket.domain.SystemMessageDomain;
import com.ws.benny.websocket.handler.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 用户状态变更事件处理
 */
@Slf4j
@Service
public class UserStateChangeEventHandler implements EventHandler {

    private static WebSocketServer webSocketServer;

    @Autowired
    public UserStateChangeEventHandler(WebSocketServer webSocketServer) {
        UserStateChangeEventHandler.webSocketServer = webSocketServer;
    }

    /**
     * 收到状态更改后的处理业务
     * 此方法将收到的消息发送到此机器的所有在线用户上
     *
     * @param systemMessageDomain
     */
    @Override
    public void doHandler(SystemMessageDomain systemMessageDomain) throws IOException {
        log.info("收到用户状态变更信息,message: {}", JSON.toJSONString(systemMessageDomain));
        //向receiverList发送消息
        List<String> receiverList = systemMessageDomain.getReceiverList();
        for (String userId : receiverList) {
            //如果用户在此机器上登录 就发消息
            if (WebSocketServer.sessionMap.containsKey(userId)) {
                //发送的时候发送公共类参数 不发送接收人列表等信息
                SocketMessageDomain socketMessageDomain = new SocketMessageDomain();
                BeanUtils.copyProperties(systemMessageDomain, socketMessageDomain);
                webSocketServer.sendMessageToUser(userId, socketMessageDomain);
            }
        }
    }

}
