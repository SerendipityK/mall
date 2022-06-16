package com.chen.mall.service.impl;


import com.chen.mall.common.Constant;
import com.chen.mall.enums.ExceptionEnum;
import com.chen.mall.exception.BusinessException;
import com.chen.mall.model.mapper.ProductMapper;
import com.chen.mall.model.pojo.Product;
import com.chen.mall.model.query.ProductListQuery;
import com.chen.mall.model.requst.AddProductReq;
import com.chen.mall.model.requst.ProductListReq;
import com.chen.mall.model.vo.CategoryVo;
import com.chen.mall.service.CategoryService;
import com.chen.mall.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void addProduct(AddProductReq product) {
        Product old = productMapper.selectByName(product.getName());
        if (old != null){
            throw new BusinessException(ExceptionEnum.NAME_EXISTED);
        }
        Product pro = new Product();
        BeanUtils.copyProperties(product,pro);
        int i = productMapper.insertSelective(pro);
        if (i == 0){
            throw new BusinessException(ExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public void update(Product product) {
        Product old = productMapper.selectByName(product.getName());
        if (old != null && old.getId().equals(product.getId())){
            throw new BusinessException(ExceptionEnum.NAME_EXISTED);
        }
        int i = productMapper.updateByPrimaryKeySelective(product);
        if (i == 0){
            throw new BusinessException(ExceptionEnum.UPDATE_FAILED);
        }

    }

    @Override
    public void deleteProduct(Integer id) {
        Product old = productMapper.selectByPrimaryKey(id);
        if (old == null){
            throw new BusinessException(ExceptionEnum.DELETE_ERROR);
        }
        int i = productMapper.deleteByPrimaryKey(id);
        if (i == 0){
            throw new BusinessException(ExceptionEnum.DELETE_ERROR);
        }
    }

    @Override
    public void deleteBatch(Integer[] ids,Integer sellStatus) {
        if (ids == null || ids.length == 0){
            throw new BusinessException(ExceptionEnum.PARAM_IS_NULL);
        }
        if (sellStatus < 0 || sellStatus > 1){
            throw new BusinessException(ExceptionEnum.PARAM_IS_NULL);
        }

        int i = productMapper.updateStatus(ids, sellStatus);
        if (i == 0){
            throw new BusinessException(ExceptionEnum.DELETE_ERROR);
        }

    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo<Product> pageInfo = new PageInfo<Product>(products);
        return pageInfo;

    }

    @Override
    public Product detail(Integer id){
        return productMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo list(ProductListReq productListReq){
        // 构建query对象
        ProductListQuery productListQuery = new ProductListQuery();
        // 搜索处理
        if (!StringUtils.isEmpty(productListReq.getKeyword())){
            String keyword = new StringBuilder().append("%")
                    .append(productListReq.getKeyword())
                    .append("%").toString();
            productListQuery.setKeyword(keyword);
        }
        // 目录处理：如果查询某个目录的商品你，不仅是需要查出该目录下得
        // ，还需要把子目录的所有商品都查询出来，所以要拿到一个目录id的List
        if (productListReq.getCategoryId() != null){
            List<CategoryVo> categoryVos = categoryService.listForUser(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVos,categoryIds);
            productListQuery.setCategroyIds(categoryIds);

        }
        // 排序处理
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
            PageHelper.startPage(productListReq.getPageNum(),productListReq.getPageSize(),orderBy);
        }else{
            PageHelper.startPage(productListReq.getPageNum(),productListReq.getPageSize());
        }
        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        return pageInfo;

    }

    private void getCategoryIds(List<CategoryVo> categoryVos,ArrayList<Integer> categoryIds){
        for (CategoryVo categoryVo : categoryVos) {
            if (categoryVo != null){
                categoryIds.add(categoryVo.getId());
                getCategoryIds(categoryVo.getChildCategory(),categoryIds);
            }
        }
    }


}
