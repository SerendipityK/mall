package com.chen.mall.service;

import com.chen.mall.model.pojo.Product;
import com.chen.mall.model.requst.AddProductReq;
import com.chen.mall.model.requst.ProductListReq;
import com.github.pagehelper.PageInfo;

public interface ProductService {
    void addProduct(AddProductReq product);

    void update(Product product);

    void deleteProduct(Integer id);

    void deleteBatch(Integer[] ids,Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);
}
