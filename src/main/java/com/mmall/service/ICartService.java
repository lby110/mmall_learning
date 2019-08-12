package com.mmall.service;

import com.mmall.common.ServiceRespose;
import com.mmall.vo.CartVo;

public interface ICartService {
    ServiceRespose<CartVo> add(Integer userId, Integer count, Integer productId);
    ServiceRespose<CartVo> update(Integer userId,Integer count, Integer productId);
    ServiceRespose<CartVo> delete(Integer userId,String productIds);
    ServiceRespose<CartVo> list(Integer userId);
    ServiceRespose<CartVo> allChecked(Integer userId,Integer checked);
    ServiceRespose<CartVo> allOneChecked(Integer userId,Integer checked,Integer productId);
    ServiceRespose<Integer> selectCount(Integer userId);
}
