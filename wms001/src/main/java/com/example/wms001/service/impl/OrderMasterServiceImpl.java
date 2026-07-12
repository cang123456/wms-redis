package com.example.wms001.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wms001.DTO.OrderDTO;
import com.example.wms001.common.BaseContext;
import com.example.wms001.common.OrderNoGenerator;
import com.example.wms001.common.QueryQageParam;
import com.example.wms001.constant.OrderConstant;
import com.example.wms001.entity.Goods;
import com.example.wms001.entity.OrderItem;
import com.example.wms001.entity.OrderMaster;
import com.example.wms001.entity.User;
import com.example.wms001.mapper.OrderMasterMapper;
import com.example.wms001.service.IGoodsService;
import com.example.wms001.service.IOrderMasterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 销售订单主表 服务实现类
 * </p>
 *
 * @author buyaola
 * @since 2026-05-11
 */
@Service
public class OrderMasterServiceImpl extends ServiceImpl<OrderMasterMapper, OrderMaster> implements IOrderMasterService {


    @Autowired
    private OrderMasterMapper orderMasterMapper;
    @Autowired
    private OrderItemServiceImpl orderItemService;
    @Autowired
    private IGoodsService goodsService;

    /**
     * 下单
     * 创建订单 和 订单详情
     */
    @Override
    public void createOrder(OrderDTO orderDTO) {

        // 生成订单编号
        String orderNo = OrderNoGenerator.generate();
        OrderMaster orderMaster = orderDTO.getOrderMaster();
        List<OrderItem> orderItemList = orderDTO.getOrderItemList();

        // 计算数据
        // == 订单总价
        BigDecimal sum = new BigDecimal(0);
        for (OrderItem orderItem : orderItemList) {
            // 获取商品信息
            Goods goods = goodsService.getById(orderItem.getGoodsId());
            
            // 设置仓库ID和分类ID
            if (goods != null) {
                orderItem.setStorageId(goods.getStorage());
                orderItem.setGoodsType(goods.getGoodsType());
                // 如果商品名称为空，从商品信息中获取
                if (orderItem.getGoodsName() == null || orderItem.getGoodsName().isEmpty()) {
                    orderItem.setGoodsName(goods.getName());
                }
            }
            
            // 单价 × 数量 = 小计
            BigDecimal itemPrice = orderItem.getSellPrice().multiply(new BigDecimal(orderItem.getGoodsNum()));
            // 累加总价（正确方式）
            sum = sum.add(itemPrice);

            orderItem.setOrderNo(orderNo);
            orderItem.setCreateTime(LocalDateTime.now());
            orderItem.setItemPrice(itemPrice);
        }

        // 属性拷贝 or 补全
        // todo1 将订单编号拷贝到 1表 和 list中的每一个订单详情中
        // == orderMaster 补全
        orderMaster.setUserId(BaseContext.getCurrentId());
        orderMaster.setOrderNo(orderNo); // 订单编号
        orderMaster.setTotalNum(orderItemList.size()); // 订单商品种类数量 or 订单详情数量
        orderMaster.setTotalPrice(sum);
        orderMaster.setOrderStatus(OrderConstant.ORDER_STATUS_WAIT_PAY); // 未支付
        orderMaster.setCreateTime(LocalDateTime.now()); // 创建时间
        // == orderItemList 补全
        // == == 上面for循环中已经完成


        // 写入数据库
        orderMasterMapper.insert(orderMaster);
        orderItemService.saveBatch(orderItemList);

    }

    @Override
    public void payed(String orderNo) {
        orderMasterMapper.payed(orderNo);
    }

    @Override
    public void cancelOrder(String orderNo) {
        orderMasterMapper.cancelOrder(orderNo);
    }

    @Override
    public void outStock(String orderNo) {
        orderMasterMapper.outStock(orderNo);
    }

    @Override
    public void finishOrder(String orderNo) {
        orderMasterMapper.finishOrder(orderNo);
    }

    @Override
    public void resetToUnpay(String orderNo) {
        orderMasterMapper.resetToUnpay(orderNo);
    }

    @Override
    public IPage pageCC(IPage<OrderMaster> page, Wrapper lambdaQueryWrapper) {
        return orderMasterMapper.pageCC(page, lambdaQueryWrapper);
    }


}
