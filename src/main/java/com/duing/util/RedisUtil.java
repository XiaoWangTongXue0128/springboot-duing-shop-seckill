package com.duing.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 设置缓存
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 设置缓存 及其有效时间
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }


    public Object get(String key) {
        if (!redisTemplate.hasKey(key)) {
            return null;
        }
        return redisTemplate.opsForValue().get(key);
    }


    public void decr(String key) {
        redisTemplate.opsForValue().decrement(key);
    }


    // 提供调用事务的方法
    public Object execute(SessionCallback sessionCallback) {
        return redisTemplate.execute(sessionCallback);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // 提供调用lua脚本的方法
    public Object execute(RedisScript script, List keyList) {
        return redisTemplate.execute(script, keyList);
    }
}
