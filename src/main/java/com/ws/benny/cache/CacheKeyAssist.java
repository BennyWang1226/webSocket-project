package com.ws.benny.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class CacheKeyAssist<T> {

    private final CacheKeyEnum cacheKeyEnum;

    private final Class<T> cacheValueClass;

    private final Object[] values;

    /**
     * @param cacheValueClass 缓存解析类型
     * @param cacheKeyEnum    缓存key
     * @param values          缓存key需要的值
     */
    public CacheKeyAssist(Class<T> cacheValueClass, CacheKeyEnum cacheKeyEnum, Object... values) {
        this.cacheValueClass = cacheValueClass;
        this.cacheKeyEnum = cacheKeyEnum;
        this.values = values;
        if (cacheKeyEnum.getValueClazz() != cacheValueClass) {
            log.warn("CacheKeyEnum 和 CacheKeyAssist 设置的存储类型不一致");
        }
    }

    /**
     * 设置过期时间
     */
    public CacheKeyAssist(Class<T> cacheValueClass, CacheKeyEnum cacheKeyEnum, Long expireTime, TimeUnit timeUnit, Object... values) {
        cacheKeyEnum.setExpireTime(expireTime);
        cacheKeyEnum.setExpireTimeUnit(timeUnit);
        this.cacheValueClass = cacheValueClass;
        this.cacheKeyEnum = cacheKeyEnum;
        this.values = values;
        if (cacheKeyEnum.getValueClazz() != cacheValueClass) {
            log.warn("CacheKeyEnum 和 CacheKeyAssist 设置的存储类型不一致");
        }
    }

    public String buildKey() {
        return cacheKeyEnum.buildKey(values);
    }

}
