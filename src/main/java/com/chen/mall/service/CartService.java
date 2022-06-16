package com.chen.mall.service;

import com.chen.mall.model.vo.CartVo;

import java.util.List;

public interface CartService {
    List<CartVo> add(Integer userId, Integer productId, Integer count);

    List<CartVo> getCartList(Integer userId);

    List<CartVo> delete(Integer userId, Integer productId);

    List<CartVo> updateSelected(Integer userId, Integer productId, Integer selected);

    List<CartVo> updateSelectedAll(Integer userId, Integer selected);
}
