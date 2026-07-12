package com.example.wms001.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.wms001.entity.OrderItem;
import com.example.wms001.entity.OrderMaster;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 订单明细表
 * </p>
 *
 * @author buyaola
 * @since 2026-05-11
 */
@Getter
@Setter
@ToString
public class OrderDTO {
    // 订单主表信息（地址、支付方式、备注...）
    private OrderMaster orderMaster;

    // 商品列表（关键！！！）
    private List<OrderItem> orderItemList;
}
