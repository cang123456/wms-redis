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
 * 订单明细表
 * </p>
 *
 * @author buyaola
 * @since 2026-05-11
 */
@Getter
@Setter
@ToString
@TableName("order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 商品ID 关联goods表
     */
    @TableField("goods_id")
    private Integer goodsId;

    /**
     * 商品名称
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 出库仓库 关联storage表
     */
    @TableField("storage_id")
    private Integer storageId;

    /**
     * 商品分类 关联goodstype表
     */
    @TableField("goods_type")
    private Integer goodsType;

    /**
     * 购买数量
     */
    @TableField("goods_num")
    private Integer goodsNum;

    /**
     * 进价（成本）
     */
    @TableField("buy_price")
    private BigDecimal buyPrice;

    /**
     * 售价
     */
    @TableField("sell_price")
    private BigDecimal sellPrice;

    /**
     * 小计金额
     */
    @TableField("item_price")
    private BigDecimal itemPrice;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
