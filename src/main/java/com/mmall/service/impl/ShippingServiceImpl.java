package com.mmall.service.impl;

import com.google.common.collect.Maps;
import com.mmall.common.ServiceRespose;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;
    public ServiceRespose add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int insertCount=shippingMapper.insert(shipping);
        if (insertCount>0){
            Map result= Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServiceRespose.createBySuccess("新建地址成功",result);
        }
        return ServiceRespose.createByErrorMeg("创建地址失败");
    }

    public ServiceRespose del(Integer userId, Integer shippingId){
        int insertCount=shippingMapper.deleteByUserIdAndShipping(userId,shippingId);
        if (insertCount>0){
            return ServiceRespose.createBySuccessMsg("删除地址成功");
        }
        return ServiceRespose.createByErrorMeg("删除地址失败");
    }
    public ServiceRespose update(Integer userId,Shipping shipping){
        shipping.setUserId(userId);
        int insertCount=shippingMapper.updateByUserId(shipping);
        if (insertCount>0){
            return ServiceRespose.createBySuccessMsg("更新地址成功");
        }
        return ServiceRespose.createByErrorMeg("更新地址失败");
    }
    public ServiceRespose select(Integer userId,Integer shippingId){
        Shipping insertCount=shippingMapper.selectByPrimaryKey(shippingId,userId);
        if (insertCount!=null){
            return ServiceRespose.createBySuccess("查询地址成功",insertCount);
        }
        return ServiceRespose.createByErrorMeg("查询地址失败");
    }
}
