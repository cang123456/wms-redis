package com.example.wms001.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * <p>
 * 封装常用 Redis 操作，统一管理 Key 的过期时间设置。
 *
 * @author wms001
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== String 类型 ====================

    /**
     * 写入字符串缓存（无过期时间，需谨慎使用）
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 写入字符串缓存并指定过期时间
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     * @param unit   时间单位
     */
    public void set(String key, Object value, long expire, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, expire, unit);
    }

    /**
     * 读取字符串缓存
     *
     * @return 值，key 不存在时返回 null
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // ==================== 通用操作 ====================

    /**
     * 删除一个或多个 key
     */
    public void delete(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete(java.util.Arrays.asList(keys));
            }
        }
    }

    /**
     * 获取 key 剩余过期时间（秒）
     *
     * @return 剩余秒数，-1 表示永不过期，-2 表示 key 不存在
     */
    public long getExpire(String key, TimeUnit unit) {
        Long expire = redisTemplate.getExpire(key, unit);
        return expire != null ? expire : -2;
    }

    /**
     * 设置 key 的过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }
}
