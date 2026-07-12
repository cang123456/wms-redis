package com.example.wms001.common;

public class BaseContext {

    public static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
    /**
     * 设置当前用户ID
     * @param id 用户ID
     */
    public static void setCurrentId(Integer id) {
        threadLocal.set(id);
    }
    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    public static Integer getCurrentId() {
        return threadLocal.get();
    }
    /**
     * 移除当前用户ID（防止内存泄漏）
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }
}