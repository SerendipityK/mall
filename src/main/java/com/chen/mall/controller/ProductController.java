package com.chen.mall.controller;

import com.chen.mall.common.ApiRestResponse;
import com.chen.mall.model.pojo.Product;
import com.chen.mall.model.requst.ProductListReq;
import com.chen.mall.service.ProductService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台商品
 */
@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @ApiOperation("商品详情")
    @PostMapping("/product/detail")
    public ApiRestResponse detail(Integer id){
        Product product = productService.detail(id);
        return ApiRestResponse.success(product);
    }

    @ApiOperation("商品详情")
    @PostMapping("/product/list")
    public ApiRestResponse list(ProductListReq productListReq){
        PageInfo list = productService.list(productListReq);
        return ApiRestResponse.success(list);
    }

}
