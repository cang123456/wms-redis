package com.example.wms001.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderNoGenerator {
    // 原子类保证线程安全，每秒自增，超过999重置
    private static final AtomicInteger SEQ = new AtomicInteger(0);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static String generate() {
        // 1. 时间戳（精确到毫秒）
        String timeStr = LocalDateTime.now().format(FORMATTER);
        // 2. 自增序号（000-999）
        int seq = SEQ.incrementAndGet() % 1000;
        if (seq == 0) {
            SEQ.set(1); // 重置，避免000
        }
        String seqStr = String.format("%03d", seq);
        // 3. 拼接：20260512153045123001
        return timeStr + seqStr;
    }
}