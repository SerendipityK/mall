package com.chen.mall.controller;

import com.chen.mall.common.ApiRestResponse;
import com.chen.mall.enums.ExceptionEnum;
import com.chen.mall.exception.BusinessException;
import com.chen.mall.filter.UserFilter;
import com.chen.mall.model.vo.CartVo;
import com.chen.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lazy
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    @ApiOperation("添加商品到购物车")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        cartService.add(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success();
    }


    @GetMapping("/list")
    @ApiOperation("购物车列表")
    public ApiRestResponse list() {
        // 内部获取用户id，防止横向越权
        List<CartVo> cartList = cartService.getCartList(UserFilter.currentUser.getId());
        return ApiRestResponse.success(cartList);
    }

    @GetMapping("/delete")
    @ApiOperation("删除购物车的某个商品")
    public ApiRestResponse delete(Integer productId) {
        // 不能传入userId，cartId,否则可以删除别人的购物车
        List<CartVo> list = cartService.delete(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(list);
    }

    /**
     * @param productId 商品id
     * @param selected  是否选中
     * @return
     */
    @PostMapping("/select")
    @ApiOperation("选中/不选中购物车的某个商品")
    public ApiRestResponse select(Integer productId, Integer selected) {
        if (selected > 1 || selected < 0) {
            throw new BusinessException(ExceptionEnum.REQUEST_PARAM_ERROR);
        }
        List<CartVo> cartVos =
                cartService.updateSelected(UserFilter.currentUser.getId(), productId, selected);
        return ApiRestResponse.success(cartVos);
    }

    @PostMapping("/selectAll")
    public ApiRestResponse selectAll(Integer selected) {
        if (selected > 1 || selected < 0) {
            throw new BusinessException(ExceptionEnum.REQUEST_PARAM_ERROR);
        }
        cartService.updateSelectedAll(UserFilter.currentUser.getId(),selected);
        return ApiRestResponse.success();
    }
}
