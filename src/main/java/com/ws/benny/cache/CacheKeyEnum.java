package com.ws.benny.cache;

import com.ws.benny.constant.ExpertManageConstant;

import java.util.concurrent.TimeUnit;

import static com.ws.benny.constant.ExpertManageConstant.EXPERT_CACHE.CACHE_PREFIX;

/**
 * 此处只更改此项目内的key生成, token认证需要common统一认证,相关的key不作处理 例如 LoginConstant下的key
 */
public enum CacheKeyEnum {

    /**
     * 登录标识 : wk-expert : user : 1 : online_flag = 1
     */
    ONLINE_FLAG
            (CACHE_PREFIX + ":user:%s:" + ExpertManageConstant.EXPERT_CACHE.ONLINE_FLAG, String.class, false);

    /**
     * @param keyFormat      参考 String.format
     * @param valueClazz     存储的值的类型
     * @param expireTime     过期时间 小于等于0为永久存储
     * @param expireTimeUnit 过期时间时间单位
     * @param cacheNull      是否存储空值，如果存储空值，将存储null字符串
     */
    CacheKeyEnum(String keyFormat, Class<?> valueClazz, Long expireTime, TimeUnit expireTimeUnit, boolean cacheNull) {
        this.keyFormat = keyFormat;
        this.valueClazz = valueClazz;
        this.expireTime = expireTime;
        this.expireTimeUnit = expireTimeUnit;
        this.cacheNull = cacheNull;
    }

    CacheKeyEnum(String keyFormat, Class<?> valueClazz, boolean cacheNull) {
        this.keyFormat = keyFormat;
        this.valueClazz = valueClazz;
        this.cacheNull = cacheNull;
    }

    String keyFormat;

    Class<?> valueClazz;

    Long expireTime;

    TimeUnit expireTimeUnit;

    boolean cacheNull;

    public String getKeyFormat() {
        return keyFormat;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public TimeUnit getExpireTimeUnit() {
        return expireTimeUnit;
    }

    public Class<?> getValueClazz() {
        return valueClazz;
    }

    public boolean isCacheNull() {
        return cacheNull;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public void setExpireTimeUnit(TimeUnit timeUnit) {
        expireTimeUnit = timeUnit;
    }

    public String buildKey(Object... values) {
        if (values == null || values.length == 0) {
            return this.keyFormat;
        }
        return String.format(this.getKeyFormat(), values);
    }

}
