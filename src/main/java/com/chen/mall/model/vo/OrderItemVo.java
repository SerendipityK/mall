package com.chen.mall.model.vo;

import lombok.Data;

@Data
public class OrderItemVo {

    private String orderNo;

    private Integer productId;

    private String productName;

    private String productImg;

    private Integer unitPrice;

    private Integer quantity;

    private Integer totalPrice;
}