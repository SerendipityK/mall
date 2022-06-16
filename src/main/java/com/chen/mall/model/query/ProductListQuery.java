package com.chen.mall.model.query;

import lombok.Data;

import java.util.List;

/**
 * 查询商品列表的Query
 */
@Data
public class ProductListQuery {
    private String keyword;

    private List<Integer>  categroyIds;
}
