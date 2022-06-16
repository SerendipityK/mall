package com.chen.mall.model.requst;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AddProductReq {

    @NotNull(message = "商品名称必填")
    @Length(min = 1,max = 10,message = "商品名称长度不能超过10个字")
    private String name;
    @NotNull(message = "商品分类必填")
    private Integer categoryId;

    @NotNull(message = "价格必填")
    @Min(value = 1,message = "价格不能小于1分")
    private Integer price;

    @NotNull(message = "库存必填")
    @Max(value = 10000,message = "库存不能超过10000")
    private Integer stock;

    @NotNull(message = "商品描述必填")
    private String detail;

    @NotNull(message = "商品图片必须上传")
    private String image;
}
