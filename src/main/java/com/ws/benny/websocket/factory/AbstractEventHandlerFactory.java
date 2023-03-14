package com.ws.benny.websocket.factory;

import com.ws.benny.websocket.handler.EventHandler;

/**
 * 事件处理抽象工厂
 */
public abstract class AbstractEventHandlerFactory {

    public abstract EventHandler getEventHandler();

}
