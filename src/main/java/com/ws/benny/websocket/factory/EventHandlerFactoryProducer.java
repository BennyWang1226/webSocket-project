package com.ws.benny.websocket.factory;

import com.ws.benny.websocket.config.ApplicationContextHolder;
import com.ws.benny.websocket.domain.MessageTypeEnum;
import com.ws.benny.websocket.factory.impl.ChatEventHandlerFactory;
import com.ws.benny.websocket.factory.impl.ConstructionReportEventHandlerFactory;
import com.ws.benny.websocket.factory.impl.OtherEventHandlerFactory;
import com.ws.benny.websocket.factory.impl.UserStateChangeEventHandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取对应事件工厂
 */
@Slf4j
@Component
public class EventHandlerFactoryProducer {

    @Autowired
    private ApplicationContextHolder applicationContextHolder;

    /**
     * 根据事件类型获取相应工厂
     *
     * @return
     */
    public AbstractEventHandlerFactory getEventHandlerFactory(MessageTypeEnum eventType) {
        switch (eventType) {
            case ONLINE:
            case OFFLINE:
                // 登录登出事件
                return applicationContextHolder.getApplicationContext().getBean(UserStateChangeEventHandlerFactory.class);
            case CHAT:
                //用户消息聊天事件
                return applicationContextHolder.getApplicationContext().getBean(ChatEventHandlerFactory.class);
            case REPORT:
                // 施工报告生成事件
                return applicationContextHolder.getApplicationContext().getBean(ConstructionReportEventHandlerFactory.class);
        }
        //其他事件不做处理
        return applicationContextHolder.getApplicationContext().getBean(OtherEventHandlerFactory.class);
    }

}
