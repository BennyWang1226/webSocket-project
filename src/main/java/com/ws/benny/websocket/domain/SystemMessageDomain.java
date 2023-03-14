package com.ws.benny.websocket.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangbinbin
 * @create 2023/3/8 15:52
 */
@Data
public class SystemMessageDomain extends ChatMessageDomain implements Serializable {

    /**
     * 接收者
     */
    private List<String> receiverList;

}
