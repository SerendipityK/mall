package com.chen.mall.service;

import com.chen.mall.model.requst.CreateOrderReq;
import com.chen.mall.model.vo.OrderVo;
import com.github.pagehelper.PageInfo;

public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVo detail(String orderNo);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);

    void pay(String orderNo);

    void deliver(String orderNo);

    /**
     * 完结订单
     * @param orderNo
     */
    void finish(String orderNo);
}
