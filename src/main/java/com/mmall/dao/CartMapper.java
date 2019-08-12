package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param("userId")Integer userId,@Param("productId")Integer productId);

    List<Cart> selectByUserId(Integer userId);
    int selectCartPProductCheckedStatusByUserId(Integer userId);
    int deleteByUserIdProducts(@Param("userId")Integer userId,@Param("productList")List<String> productList);

    int checkedAllOrUnCheckAll(@Param("userId")Integer userId,@Param("checked") Integer checked,@Param("productId") Integer productId);
    int selectTotal(Integer userId);
}