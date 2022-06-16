package com.chen.mall.controller;

import com.chen.mall.common.ApiRestResponse;
import com.chen.mall.model.requst.CreateOrderReq;
import com.chen.mall.model.vo.OrderVo;
import com.chen.mall.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/create")
    @ApiOperation("创建订单")
    public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq){
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @GetMapping("/order/detail")
    @ApiOperation("订单详情")
    public ApiRestResponse detail(@RequestParam String orderNo){
        OrderVo orderVo = orderService.detail(orderNo);
        return ApiRestResponse.success(orderVo);
    }

    @GetMapping("/order/list")
    @ApiOperation("前台订单列表")
    public ApiRestResponse list(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    /**
     * 前台取消订单
     * @param orderNo
     * @return
     */
    @PostMapping("/order/cancel")
    @ApiOperation("前台取消订单")
    public ApiRestResponse cancel(@RequestParam String orderNo){
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    @PostMapping("/order/qrcode")
    @ApiOperation("生成支付二维码")
    public ApiRestResponse qrcode(@RequestParam String orderNo){
        String pngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }

    @GetMapping("/order/pay")
    @ApiOperation("支付")
    public ApiRestResponse pay(String orderNo){
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }



}
