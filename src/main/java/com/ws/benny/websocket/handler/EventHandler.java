package com.ws.benny.websocket.handler;

import com.ws.benny.websocket.domain.SystemMessageDomain;

import java.io.IOException;

/**
 * 事件处理
 */
public interface EventHandler {

    void doHandler(SystemMessageDomain systemMessageDomain) throws IOException;

}
