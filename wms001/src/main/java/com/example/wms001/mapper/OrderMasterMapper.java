package com.example.wms001.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.wms001.entity.OrderMaster;
import com.example.wms001.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;


/**
 * <p>
 * 销售订单主表 Mapper 接口
 * </p>
 *
 * @author buyaola
 * @since 2026-05-11
 */
@Mapper
public interface OrderMasterMapper extends BaseMapper<OrderMaster> {


    void payed(Integer id);

    // 支付成功
    void payed(String orderNo);

    // 取消订单
    void cancelOrder(String orderNo);

    // 订单出库
    void outStock(String orderNo);

    // 订单完成
    void finishOrder(String orderNo);

    // 重置为待支付
    void resetToUnpay(String orderNo);

    IPage pageCC(IPage<OrderMaster> page, @Param(Constants.WRAPPER) Wrapper<OrderMaster> wrapper);
}
