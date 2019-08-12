package com.mmall.service;

import com.mmall.common.ServiceRespose;
import com.mmall.pojo.Shipping;

public interface IShippingService {
    ServiceRespose add(Integer userId, Shipping shipping);
    ServiceRespose del(Integer userId, Integer shippingId);
    ServiceRespose update(Integer userId,Shipping shipping);
    ServiceRespose select(Integer userId,Integer shippingId);
}
