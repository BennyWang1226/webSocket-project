package com.ws.benny.websocket.handler.impl;

import com.ws.benny.websocket.domain.SystemMessageDomain;
import com.ws.benny.websocket.handler.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 施工报告通知事件
 */
@Slf4j
@Service
public class ConstructionReportEventHandler implements EventHandler {

    @Override
    public void doHandler(SystemMessageDomain systemMessageDomain) {
        log.info("收到施工报告生成消息");
    }

}
