package com.example.wms001.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 销售订单主表
 * </p>
 *
 * @author buyaola
 * @since 2026-05-11
 */
@Getter
@Setter
@ToString
@TableName("order_master")
public class OrderMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号（唯一）
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 下单用户ID 关联user表id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 商品总数量
     */
    @TableField("total_num")
    private Integer totalNum;

    /**
     * 订单总金额
     */
    @TableField("total_price")
    private BigDecimal totalPrice;

    /**
     * 订单状态：1=待支付 2=已支付 3=已出库 4=已完成 5=已取消
     */
    @TableField("order_status")
    private Byte orderStatus;

    /**
     * 订单备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 出库时间
     */
    @TableField("out_time")
    private LocalDateTime outTime;

    /**
     * 完成时间
     */
    @TableField("finish_time")
    private LocalDateTime finishTime;
}
