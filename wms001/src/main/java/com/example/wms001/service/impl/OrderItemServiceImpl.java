package com.example.wms001.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wms001.entity.OrderItem;
import com.example.wms001.mapper.OrderItemMapper;
import com.example.wms001.service.IOrderItemService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author buyaola
 * @since 2026-05-11
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
