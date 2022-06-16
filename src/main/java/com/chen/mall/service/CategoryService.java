package com.chen.mall.service;

import com.chen.mall.model.pojo.Category;
import com.chen.mall.model.requst.AddCategoryReq;
import com.chen.mall.model.vo.CategoryVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVo> listForUser(Integer parentId);
}
