package com.ws.benny.constant;

public interface ExpertManageConstant {

    /**
     * redis发布订阅websocket主题名称
     */
    interface WEBSOCKET {
        String REDIS_TOPIC = "wsTopic";
    }

    /**
     * 缓存keyName
     */
    interface EXPERT_CACHE {
        String CACHE_PREFIX = "projectName";
        // 登录标识
        String ONLINE_FLAG = "online_flag";
    }

    interface ENGINEER_CACHE {
        String CACHE_ENGINEER_PREFIX = "wk-engineer";
    }

}