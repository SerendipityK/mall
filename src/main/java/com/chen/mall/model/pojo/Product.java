package com.chen.mall.model.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Product {
    private Integer id;

    private String name;

    private String image;

    private String detail;

    private Integer categoryId;

    private Integer price;

    private Integer stock;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}