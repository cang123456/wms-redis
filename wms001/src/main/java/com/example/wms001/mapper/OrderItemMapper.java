package com.example.wms001.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wms001.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 * 订单明细表 Mapper 接口
 * </p>
 *
 * @author buyaola
 * @since 2026-05-11
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

}
