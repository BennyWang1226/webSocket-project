package com.ws.benny.service.impl;

import com.ws.benny.service.HandlerService;
import com.ws.benny.websocket.domain.SystemMessageDomain;
import com.ws.benny.websocket.factory.EventHandlerFactoryProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HandlerServiceImpl implements HandlerService {

    @Autowired
    private EventHandlerFactoryProducer eventHandlerFactoryProducer;

    /**
     * 调用处理接口处理业务逻辑，重试3次
     */
    @Retryable
    @Override
    public void handler(SystemMessageDomain systemMessageDomain) {
        log.info("开始处理业务逻辑");
        try {
            //根据事件类型分发任务
            eventHandlerFactoryProducer.getEventHandlerFactory(systemMessageDomain.getType()).getEventHandler().doHandler(systemMessageDomain);
        } catch (Exception e) {
            log.error("处理业务异常, : {}", e.getMessage());
        }
        log.info("业务逻辑处理完毕");
    }

}
