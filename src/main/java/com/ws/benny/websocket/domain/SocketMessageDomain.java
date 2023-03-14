package com.ws.benny.websocket.domain;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangbinbin
 * @create 2023/3/8 9:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketMessageDomain implements Serializable {

    /**
     * 消息类型
     */
    private MessageTypeEnum type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime = new Date();

    /**
     * 创建message : ONLINE OFFLINE
     */
    public static String createPongMessage() {
        SocketMessageDomain messageDomain = new SocketMessageDomain();
        messageDomain.setType(MessageTypeEnum.PONG);
        return JSON.toJSONString(messageDomain);
    }

}
