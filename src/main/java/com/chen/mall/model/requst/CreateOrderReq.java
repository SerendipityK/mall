package com.chen.mall.model.requst;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateOrderReq {

    @NotNull
    private String receiverName;

    @NotNull
    private String receiverMobile;

    @NotNull
    private String receiverAddress;

    private Integer postage = 0;  // 默认包邮

    private Integer paymentType = 1; // 默认在线支付

}