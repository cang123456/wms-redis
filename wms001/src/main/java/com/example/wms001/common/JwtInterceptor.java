package com.example.wms001.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest; // 关键：jakarta而非javax
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil; // 注入common包的JwtUtil

    @Autowired
    private TokenBlacklistService tokenBlacklistService; // 注入Token黑名单服务

    @Value("${jwt.header}")
    private String Header;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 排除登录和注册接口
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/user/login") || requestURI.contains("/user/register")) {
            return true;
        }
        // 放行OPTIONS预检请求
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 2. 放行 Knife4j 接口文档（关键！）
        if (requestURI.contains("/doc.html")
                || requestURI.contains("/webjars/")
                || requestURI.contains("/v3/api-docs")) {
            return true;
        }

        // 获取Token
        String token = request.getHeader(Header);
        if (token == null) {
            throw new RuntimeException("请先登录");
        }

        token = token.replace("Bearer ", "");

        // 校验 Token 是否在黑名单中（登出后失效的 Token）
        if (tokenBlacklistService.isBlacklisted(token)) {
            throw new RuntimeException("Token已失效，请重新登录");
        }

        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token无效或已过期");
        }

        // 存入用户ID到请求域
        String userId = jwtUtil.getUserIdFromToken(token);
        BaseContext.setCurrentId(Integer.valueOf(userId));

        return true;
    }
}
