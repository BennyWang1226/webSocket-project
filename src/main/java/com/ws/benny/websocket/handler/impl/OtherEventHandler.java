package com.ws.benny.websocket.handler.impl;

import com.ws.benny.websocket.domain.SystemMessageDomain;
import com.ws.benny.websocket.handler.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 其他事件 预留位 不处理消息
 */
@Slf4j
@Service
public class OtherEventHandler implements EventHandler {

    @Override
    public void doHandler(SystemMessageDomain systemMessageDomain) {
//        log.info("收到其他事件类型,不需要处理");
    }

}
