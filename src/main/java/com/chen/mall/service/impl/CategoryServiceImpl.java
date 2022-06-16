package com.chen.mall.service.impl;

import com.chen.mall.enums.ExceptionEnum;
import com.chen.mall.exception.BusinessException;
import com.chen.mall.model.mapper.CategoryMapper;
import com.chen.mall.model.pojo.Category;
import com.chen.mall.model.requst.AddCategoryReq;
import com.chen.mall.model.vo.CategoryVo;
import com.chen.mall.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void add(AddCategoryReq addCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq,category);
        Category c = categoryMapper.selectName(addCategoryReq.getName());
        if (c != null){
            throw new BusinessException(ExceptionEnum.CATEGORY_NAME_EXISTED);
        }
        Integer type = addCategoryReq.getType();
        if (type > 3){
            throw new BusinessException(ExceptionEnum.CATEGORY_TYPE_OUT);
        }
        int i = categoryMapper.insertSelective(category);
        if (i == 0){
            throw new BusinessException(ExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public void update(Category updateCategory) {
        if (updateCategory.getName() != null){
            Category categoryOld = categoryMapper.selectName(updateCategory.getName());
            if (categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())){
                throw new BusinessException(ExceptionEnum.NAME_EXISTED);
            }
        }
        int i = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (i == 0){
            throw new BusinessException(ExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        int i = categoryMapper.deleteByPrimaryKey(id);
        if (i == 0){
            throw new BusinessException(ExceptionEnum.DELETE_ERROR);
        }
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        // type为第一优先级，order_num为第二优先级
        PageHelper.startPage(pageNum,pageSize,"type,order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);
        return pageInfo;
    }

    @Cacheable(value = "listForUser")
    @Override
    public List<CategoryVo> listForUser(Integer parentId) {
        List<CategoryVo> list = new ArrayList<>();
        find(list,parentId);
        return list;
    }

    public void find(List<CategoryVo> list,Integer parentId){
        List<Category> categories = categoryMapper.selectByParentId(parentId);
        if (!CollectionUtils.isEmpty(categories)){
            for (Category category : categories) {
                CategoryVo categoryVo = new CategoryVo();
                BeanUtils.copyProperties(category,categoryVo);
                list.add(categoryVo);
                find(categoryVo.getChildCategory(),categoryVo.getId());
            }
        }
    }
}
