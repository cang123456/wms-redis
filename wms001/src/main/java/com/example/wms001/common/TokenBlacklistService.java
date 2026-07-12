package com.example.wms001.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * JWT Token 黑名单服务
 * <p>
 * 解决 JWT 无状态导致无法主动失效的问题：
 * <ul>
 *   <li>用户登出时，将 Token 加入黑名单（TTL = Token 剩余有效时间）</li>
 *   <li>拦截器校验时优先检查黑名单，命中则拒绝请求</li>
 * </ul>
 * <p>
 * Key 格式：jwt:blacklist:{token}
 * Value：固定值 "1"
 * TTL：Token 剩余有效秒数（随 Token 自然过期自动清除，不占用额外内存）
 *
 * @author wms001
 */
@Slf4j
@Service
public class TokenBlacklistService {

    /** Redis Key 前缀 */
    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 将 Token 加入黑名单
     * <p>
     * TTL 设置为 Token 的剩余有效时间——Token 本身过期后，黑名单记录也自动清除，
     * 不会产生内存泄漏。
     *
     * @param token 需要失效的 JWT Token
     */
    public void addToBlacklist(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        // 清除可能遗留的 "Bearer " 前缀
        token = token.replace("Bearer ", "");

        long remainingSeconds = jwtUtil.getTokenRemainingSeconds(token);
        if (remainingSeconds <= 0) {
            log.debug("Token 已过期，无需加入黑名单");
            return;
        }

        String key = BLACKLIST_KEY_PREFIX + token;
        redisUtil.set(key, "1", remainingSeconds, TimeUnit.SECONDS);
        log.info("Token 已加入黑名单，剩余有效时间 {} 秒", remainingSeconds);
    }

    /**
     * 检查 Token 是否在黑名单中
     *
     * @param token JWT Token
     * @return true = 已在黑名单，应拒绝请求
     */
    public boolean isBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        token = token.replace("Bearer ", "");
        return redisUtil.hasKey(BLACKLIST_KEY_PREFIX + token);
    }
}
