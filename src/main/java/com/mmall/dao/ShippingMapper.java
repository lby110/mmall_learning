package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(@Param("id") Integer id,@Param("userId") Integer userId);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);
    int deleteByUserIdAndShipping(@Param("userId")Integer userId,@Param("shippingId")Integer shippingId);
    int updateByUserId(Shipping record);
}