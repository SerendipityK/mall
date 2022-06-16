package com.chen.mall.service.impl;

import com.chen.mall.common.Constant;
import com.chen.mall.enums.ExceptionEnum;
import com.chen.mall.exception.BusinessException;
import com.chen.mall.model.mapper.CartMapper;
import com.chen.mall.model.mapper.ProductMapper;
import com.chen.mall.model.pojo.Cart;
import com.chen.mall.model.pojo.Product;
import com.chen.mall.model.vo.CartVo;
import com.chen.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<CartVo> add(Integer userId, Integer productId, Integer count){
        validateProduct(productId,count);
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null){
            // 创建
            cart = new Cart();
            cart.setProductId(productId);
            cart.setQuantity(1);
            cart.setSelected(Constant.Cart.CHECK);
            cart.setUserId(userId);
            cartMapper.insertSelective(cart);
        }else{
            count = cart.getQuantity() + count;
            Cart cartNew = new Cart();
            cartNew.setProductId(productId);
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setSelected(Constant.Cart.CHECK);
            cartNew.setUserId(userId);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.getCartList(userId);
    }

    @Override
    public List<CartVo> getCartList(Integer userId){
        List<CartVo> cartVos = cartMapper.selectByUserId(userId);
        for (CartVo cartVo : cartVos) {
            cartVo.setTotalPrice(cartVo.getPrice() * cartVo.getQuantity());
        }
        return cartVos;
    }

    @Override
    public List<CartVo> delete(Integer userId,Integer productId){
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null){
            throw new BusinessException(ExceptionEnum.PRODUCT_NOT_FIND);
        }
        int i = cartMapper.deleteByPrimaryKey(cart.getId());
        if (i == 0){
            throw new BusinessException(ExceptionEnum.DELETE_ERROR);
        }
        return this.getCartList(userId);
    }

    @Override
    public List<CartVo> updateSelected(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null){
            throw new BusinessException(ExceptionEnum.PRODUCT_NOT_FIND);
        }
        int i = cartMapper.updateSelected(userId, productId, selected);
        if (i == 0){
            throw new BusinessException(ExceptionEnum.UPDATE_FAILED);
        }
        return this.getCartList(userId);
    }

    @Override
    public List<CartVo> updateSelectedAll(Integer userId, Integer selected){
        // 查询购物车是否有东西
        List<CartVo> cartVos = cartMapper.selectByUserId(userId);
        if (cartVos.size() == 0){
            throw new BusinessException(ExceptionEnum.CART_IS_EMPTY);
        }
        int i = cartMapper.updateAllSelected(userId, selected);
        if (i == 0){
            throw new BusinessException(ExceptionEnum.UPDATE_FAILED);
        }
        return this.getCartList(userId);
    }

    /**
     * 校验商品
     * @param productId
     * @param count
     */
    private void validateProduct(Integer productId, Integer count) {
        // 判断商品是否存在，是否上架
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null || product.getStatus() == Constant.ProductStatus.TAKE_DOWN){
            throw new BusinessException(ExceptionEnum.PRODUCT__NOT_BUY);
        }
        // 判断库存是否够
        if (product.getStock() == 0){
            throw new BusinessException(ExceptionEnum.NOT_ENOUGH_STOCK);
        }
    }

    public List<CartVo> selectOrNot(Integer userId,Integer productId,Integer selected){
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null){
            throw new BusinessException(ExceptionEnum.PRODUCT_NOT_FIND);
        }else{
            cartMapper.updateSelected(userId,productId,selected);
        }
        return this.getCartList(userId);
    }


}
