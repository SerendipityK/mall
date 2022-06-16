package com.chen.mall.model.mapper;

import com.chen.mall.model.pojo.Cart;
import com.chen.mall.model.vo.CartVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(Integer userId,Integer productId);

    List<CartVo> selectByUserId(Integer userId);

    int deleteByUserIdAndProductId(Integer userId, Integer productId);

    int updateSelected(Integer userId, Integer productId, Integer selected);

    int updateAllSelected(Integer userId, Integer selected);
}