package com.ws.benny.websocket.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangbinbin
 * @create 2023/3/8 15:52
 */
@Data
public class ChatMessageDomain extends SocketMessageDomain implements Serializable {

    /**
     * 发送者
     */
    private String sender;

}
