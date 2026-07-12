package com.example.wms001.constant;

public class OrderConstant {

    /**
     * 订单相关常量
     * 订单状态：1=待支付 2=已支付 3=已出库 4=已完成 5=已取消
     */
    public static final Byte ORDER_STATUS_WAIT_PAY = 1;
    public static final Byte ORDER_STATUS_PAID = 2;
    public static final Byte ORDER_STATUS_OUT_STOCK = 3;
    public static final Byte ORDER_STATUS_COMPLETE = 4;
    public static final Byte ORDER_STATUS_CANCEL = 5;


    // 支付方式 1微信 2支付宝
    public static final Byte PAY_TYPE_WECHAT = 1;
    public static final Byte PAY_TYPE_ALIPAY = 2;

}