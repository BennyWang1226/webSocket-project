package com.ws.benny.service;

import com.ws.benny.websocket.domain.SystemMessageDomain;

public interface HandlerService {

    void handler(SystemMessageDomain systemMessageDomain);

}
