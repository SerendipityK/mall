package com.chen.mall.controller;

import com.chen.mall.common.ApiRestResponse;
import com.chen.mall.model.pojo.Category;
import com.chen.mall.model.requst.AddCategoryReq;
import com.chen.mall.model.requst.UpdateCategoryReq;
import com.chen.mall.model.vo.CategoryVo;
import com.chen.mall.service.CategoryService;
import com.chen.mall.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    /**
     * 后台添加目录
     */
    @ApiOperation(value = "后台添加目录",notes = "根据category对象创建目录")
    @PostMapping("/admin/category/add")
    public ApiRestResponse<Void> add(@Valid @RequestBody AddCategoryReq addCategoryReq){
        categoryService.add(addCategoryReq);
        return ApiRestResponse.success();
    }

    @ApiOperation(value = "后台更新目录")
    @PostMapping("/admin/category/update")
    public ApiRestResponse update(@Valid @RequestBody UpdateCategoryReq categoryReq){
        Category category = new Category();
        BeanUtils.copyProperties(categoryReq,category);
        categoryService.update(category);
        return ApiRestResponse.success();
    }

    @ApiOperation(value = "后台删除目录")
    @PostMapping("/admin/category/delete")
    public ApiRestResponse delete(Integer id){
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    /**
     *
     * @param pageNum 当前页码
     * @param pageSize  每页大小
     * @return
     */
    @ApiOperation("后台目录列表")
    @GetMapping("/admin/category/list")
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("后台目录列表")
    @GetMapping("/category/list")
    public ApiRestResponse listCategory(){
        List<CategoryVo> categoryVos = categoryService.listForUser(0);
        return ApiRestResponse.success(categoryVos);
    }
}
