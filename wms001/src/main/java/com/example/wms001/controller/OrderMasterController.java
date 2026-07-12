package com.example.wms001.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.wms001.DTO.OrderDTO;
import com.example.wms001.common.BaseContext;
import com.example.wms001.common.QueryQageParam;
import com.example.wms001.common.Result;
import com.example.wms001.entity.OrderMaster;
import com.example.wms001.service.IOrderItemService;
import com.example.wms001.service.IOrderMasterService;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static com.baomidou.mybatisplus.extension.ddl.DdlScriptErrorHandler.PrintlnLogErrorHandler.log;

/**
 * <p>
 * 销售订单主表 前端控制器
 * </p>
 *
 * @author buyaola
 * @since 2026-05-11
 */
@RestController
@RequestMapping("/order")
@Slf4j
@Tag(name = "用户下单", description = "用户下单接口")
public class OrderMasterController {

    @Autowired
    private IOrderMasterService orderMasterService;
    @Autowired
    private IOrderItemService orderItemService;




    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryQageParam query) {
        log.info("分页查询listPageC1 {}",query);

        Page<OrderMaster> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<OrderMaster> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        IPage result = orderMasterService.pageCC(page, lambdaQueryWrapper);

        return Result.suc(result.getRecords(),result.getTotal());
    }


    /**
     * 下单
     * 创建订单 和 订单详情
     */
    @PostMapping("/createOrder")
    public Result createOrder(@RequestBody OrderDTO orderDTO) {
        // 订单-订单详情  的对应关系  1：（1-n）
        // 下单--创建订单
        log.info("下单--创建订单: {}",orderDTO);
        orderMasterService.createOrder(orderDTO);
        BaseContext.removeCurrentId();
        return Result.suc("下单成功");
    }


    /**
     * 支付订单（待支付 → 已支付）
     */
    @PostMapping("/payed")
    public Result payed(@RequestParam String orderNo) {
        orderMasterService.payed(orderNo);
        return Result.suc("支付成功");
    }

    /**
     * 取消订单（任意可取消状态 → 已取消）
     */
    @PostMapping("/cancelOrder")
    public Result cancelOrder(@RequestParam String orderNo) {
        orderMasterService.cancelOrder(orderNo);
        return Result.suc("取消订单成功");
    }

    /**
     * 订单出库（已支付 → 已出库）
     */
    @PostMapping("/outStock")
    public Result outStock(@RequestParam String orderNo) {
        orderMasterService.outStock(orderNo);
        return Result.suc("订单出库成功");
    }

    /**
     * 完成订单（已出库 → 已完成）
     */
    @PostMapping("/finishOrder")
    public Result finishOrder(@RequestParam String orderNo) {
        orderMasterService.finishOrder(orderNo);
        return Result.suc("订单已完成");
    }

    /**
     * 重置为待支付
     */
    @PostMapping("/resetToUnpay")
    public Result resetToUnpay(@RequestParam String orderNo) {
        orderMasterService.resetToUnpay(orderNo);
        return Result.suc("重置为待支付成功");
    }


}
