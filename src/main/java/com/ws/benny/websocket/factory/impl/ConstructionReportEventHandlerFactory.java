package com.ws.benny.websocket.factory.impl;

import com.ws.benny.websocket.config.ApplicationContextHolder;
import com.ws.benny.websocket.factory.AbstractEventHandlerFactory;
import com.ws.benny.websocket.handler.EventHandler;
import com.ws.benny.websocket.handler.impl.ConstructionReportEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 施工报告事件工厂
 */
@Slf4j
@Component
public class ConstructionReportEventHandlerFactory extends AbstractEventHandlerFactory {

    @Autowired
    private ApplicationContextHolder applicationContextHolder;

    @Override
    public EventHandler getEventHandler() {
        return applicationContextHolder.getApplicationContext().getBean(ConstructionReportEventHandler.class);
    }

}
