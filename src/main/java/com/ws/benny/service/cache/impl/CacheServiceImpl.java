package com.ws.benny.service.cache.impl;

import com.alibaba.fastjson.JSON;
import com.ws.benny.cache.CacheGetNullCallback;
import com.ws.benny.cache.CacheKeyAssist;
import com.ws.benny.service.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheServiceImpl<T> implements CacheService<T> {

    private static final String CACHE_NULL_STRING_VALUE = "null";

    private static final Long CACHE_NULL_NUMBER_VALUE = 0L;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public T get(CacheKeyAssist<T> cacheKey) {
        return JSON.parseObject(stringRedisTemplate.opsForValue().get(cacheKey.buildKey()), cacheKey.getCacheValueClass());
    }

    @Override
    public T get(CacheKeyAssist<T> cacheKey, CacheGetNullCallback<T> callback) {
        String retString = stringRedisTemplate.opsForValue().get(cacheKey.buildKey());
        if (retString != null) {
            return JSON.parseObject(retString, cacheKey.getCacheValueClass());
        }
        try {
            synchronized (CacheService.class) {
                retString = stringRedisTemplate.opsForValue().get(cacheKey.buildKey());
                if (retString != null) {
                    return JSON.parseObject(retString, cacheKey.getCacheValueClass());
                }
                if (callback == null) {
                    return null;
                }
                T callbackData = callback.doCallback();
                if (callbackData == null && !cacheKey.getCacheKeyEnum().isCacheNull()) {
                    return null;
                }
                set(cacheKey, callbackData);
                return callbackData;
            }
        } catch (Exception e) {
            log.warn("执行 redis 返回为 null 执行回调时出现错误", e);
        }
        return null;
    }

    @Override
    public void set(CacheKeyAssist<T> cacheKey, T value) {
        if (value == null && !cacheKey.getCacheKeyEnum().isCacheNull()) {
            return;
        }
//        String cacheStringValue = (value == null ? (Number.class.isAssignableFrom(cacheKey.getCacheKeyEnum().getValueClazz()) ? CACHE_NULL_NUMBER_VALUE.toString() : CACHE_NULL_STRING_VALUE) : JSON.toJSONString(value));
        String cacheStringValue = String.class.isAssignableFrom(cacheKey.getCacheKeyEnum().getValueClazz()) ? String.valueOf(value) : JSON.toJSONString(value);
        if (cacheKey.getCacheKeyEnum().getExpireTime() != null && cacheKey.getCacheKeyEnum().getExpireTime() > 0L) {
            stringRedisTemplate.opsForValue().set(cacheKey.buildKey(), cacheStringValue, cacheKey.getCacheKeyEnum().getExpireTime(), cacheKey.getCacheKeyEnum().getExpireTimeUnit());
        } else {
            stringRedisTemplate.opsForValue().set(cacheKey.buildKey(), cacheStringValue);
        }
    }

    @Override
    public Boolean delete(CacheKeyAssist<T> cacheKey) {
        return stringRedisTemplate.delete(cacheKey.buildKey());
    }

    @Override
    public Long increment(CacheKeyAssist<T> cacheKey, CacheGetNullCallback<T> callback) {
        return increment(cacheKey, 1L, callback);
    }

    @Override
    public Long increment(CacheKeyAssist<T> cacheKey, Long delta) {
        return increment(cacheKey, delta, null);
    }

    @Override
    public Long increment(CacheKeyAssist<T> cacheKey, Long delta, CacheGetNullCallback<T> callback) {
        Boolean hasKey = stringRedisTemplate.hasKey(cacheKey.buildKey());
        if (Boolean.TRUE.equals(hasKey)) {
            return stringRedisTemplate.opsForValue().increment(cacheKey.buildKey(), delta);
        }
//        return JSON.parseObject(get(cacheKey, callback).toString(), Long.class);
        return null;
    }

    @Override
    public void expire(CacheKeyAssist<T> cacheKey) {
        stringRedisTemplate.expire(cacheKey.buildKey(), cacheKey.getCacheKeyEnum().getExpireTime(), cacheKey.getCacheKeyEnum().getExpireTimeUnit());
    }

    @Override
    public void expire(CacheKeyAssist<T> cacheKey, String value) {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(cacheKey.buildKey()))) {
            expire(cacheKey);
        } else {
            if (cacheKey.getCacheKeyEnum().getExpireTime() != null && cacheKey.getCacheKeyEnum().getExpireTime() > 0L) {
                stringRedisTemplate.opsForValue().set(cacheKey.buildKey(), value, cacheKey.getCacheKeyEnum().getExpireTime(), cacheKey.getCacheKeyEnum().getExpireTimeUnit());
            } else {
                stringRedisTemplate.opsForValue().set(cacheKey.buildKey(), value);
            }
        }
    }

}
