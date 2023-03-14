package com.ws.benny.websocket.election;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wangbinbin
 * @create 2023/3/12 22:45
 */
@Deprecated
public class MainNodeSelector {
    // 定义主节点的选取策略
    public enum Strategy {
        RANDOM, ROUND_ROBIN
    }

    private List<String> nodes; // 所有节点的列表
    private Strategy strategy; // 选取主节点的策略
    private int currentIndex; // 当前选取的主节点在节点列表中的索引

    public MainNodeSelector(List<String> nodes, Strategy strategy) {
        this.nodes = new CopyOnWriteArrayList<>(nodes); // 用线程安全的CopyOnWriteArrayList来存储节点列表
        this.strategy = strategy;
        this.currentIndex = -1; // 初始化为-1，表示当前还没有选取过主节点
    }

    public String selectMainNode() {
        switch (strategy) {
            case RANDOM:
                return selectRandomNode();
            case ROUND_ROBIN:
                return selectRoundRobinNode();
            default:
                return null;
        }
    }

    private String selectRandomNode() {
        int index = (int) (Math.random() * nodes.size());
        return nodes.get(index);
    }

    private String selectRoundRobinNode() {
        currentIndex++;
        if (currentIndex >= nodes.size()) {
            currentIndex = 0;
        }
        return nodes.get(currentIndex);
    }

    public void addNode(String node) {
        nodes.add(node);
    }

    public void removeNode(String node) {
        nodes.remove(node);
    }

}
