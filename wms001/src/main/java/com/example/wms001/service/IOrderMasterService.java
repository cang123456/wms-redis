package com.example.wms001.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wms001.DTO.OrderDTO;
import com.example.wms001.common.QueryQageParam;
import com.example.wms001.entity.OrderMaster;
import com.example.wms001.entity.User;

import java.util.HashMap;

/**
 * <p>
 * 销售订单主表 服务类
 * </p>
 *
 * @author buyaola
 * @since 2026-05-11
 */
public interface IOrderMasterService extends IService<OrderMaster> {

    /**
     * 下单
     * 创建订单 和 订单详情
     */
    void createOrder(OrderDTO orderDTO);

    void payed(String orderNo);
    void cancelOrder(String orderNo);
    void outStock(String orderNo);
    void finishOrder(String orderNo);
    void resetToUnpay(String orderNo);

    IPage pageCC(IPage<OrderMaster> page, Wrapper lambdaQueryWrapper);
}
