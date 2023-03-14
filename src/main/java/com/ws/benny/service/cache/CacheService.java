package com.ws.benny.service.cache;

import com.ws.benny.cache.CacheGetNullCallback;
import com.ws.benny.cache.CacheKeyAssist;

public interface CacheService<T> {

    T get(CacheKeyAssist<T> cacheKey);

    T get(CacheKeyAssist<T> cacheKey, CacheGetNullCallback<T> callback);

    void set(CacheKeyAssist<T> cacheKey, T value);

    Boolean delete(CacheKeyAssist<T> cacheKey);

    Long increment(CacheKeyAssist<T> cacheKey, CacheGetNullCallback<T> callback);

    Long increment(CacheKeyAssist<T> cacheKey, Long delta);

    Long increment(CacheKeyAssist<T> cacheKey, Long delta, CacheGetNullCallback<T> callback);

    void expire(CacheKeyAssist<T> cacheKey);

    void expire(CacheKeyAssist<T> cacheKey, String value);

}
