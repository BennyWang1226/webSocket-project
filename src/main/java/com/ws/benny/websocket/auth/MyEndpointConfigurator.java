package com.ws.benny.websocket.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;

import static javax.websocket.server.HandshakeRequest.SEC_WEBSOCKET_PROTOCOL;

/**
 * 将token传入Sec-WebSocket-Protocol,进行握手前鉴权
 *
 * @author wangbinbin
 * @create 2023/3/7 9:45
 */
@Slf4j
public class MyEndpointConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 1.取出Protocol信息
        List<String> list = request.getHeaders().get(SEC_WEBSOCKET_PROTOCOL);
        String userId = null;
        try {
            // 2.获取token
            String token = list.get(0);
            log.info("客户端连接websocket,执行握手前鉴权,token: {}", token);
            // TODO 此处为鉴权策略
            // 3.根据token获取userId
            userId = token;
            // 4.将用户身份信息放入WebSocketSession的Properties属性中
            sec.getUserProperties().put("userId", userId);
        } catch (Exception e) {
            log.info("客户端解析密文失败,e: {}", e.getMessage());
            throw new RuntimeException("解析token失败");
        }
        log.info("token解析完成,userId: {}", userId);
        /**
         * 注意:
         * websocket规范要求: 如果传递了Sec-WebSocket-Protocol参数,也必须在response中返回,
         * 否则给客户端发送消息时会抛出java.io.IOException: 你的主机中的软件中止了一个已建立的连接
         * 可以在ServerEndpointConfig中设置 也可以用servlet过滤器来做
         */
        if (CollectionUtils.isNotEmpty(list))
            response.getHeaders().put(SEC_WEBSOCKET_PROTOCOL, list);
        super.modifyHandshake(sec, request, response);
    }

    @Override
    public boolean checkOrigin(String originHeaderValue) {
        // 进行身份验证，返回true或false
        return true;
    }

}
