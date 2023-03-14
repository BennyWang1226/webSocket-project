package com.ws.benny.websocket.election;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangbinbin
 * @create 2023/3/12 22:46
 */
@Deprecated
public class WebSocketService {
    private static final List<String> NODES = Arrays.asList("node1", "node2", "node3");
    private static final MainNodeSelector MAIN_NODE_SELECTOR = new MainNodeSelector(NODES, MainNodeSelector.Strategy.ROUND_ROBIN);

    public void send(String message) {
        String mainNode = MAIN_NODE_SELECTOR.selectMainNode();
        // 将消息发送到主节点
        // ...
    }

    public void onNodeJoin(String node) {
        MAIN_NODE_SELECTOR.addNode(node);
    }

    public void onNodeLeave(String node) {
        MAIN_NODE_SELECTOR.removeNode(node);
    }
}
