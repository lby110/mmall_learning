package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServiceRespose;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;

    /**
     *
     * @param session
     * @param shipping
     * @return
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServiceRespose add(HttpSession session, Shipping shipping){
       User user= (User) session.getAttribute(Const.CURRENT_USER);
       if (user==null){
           return ServiceRespose.createByErrorMeg("用户未登录");
       }
       return iShippingService.add(user.getId(),shipping);
    }

    /**
     * 删除地址
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping("del.do")
    @ResponseBody
    public ServiceRespose del(HttpSession session, Integer shippingId){
       User user= (User) session.getAttribute(Const.CURRENT_USER);
       if (user==null){
           return ServiceRespose.createByErrorMeg("用户未登录");
       }
       return iShippingService.del(user.getId(),shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServiceRespose update(HttpSession session,Shipping shipping){
       User user= (User) session.getAttribute(Const.CURRENT_USER);
       if (user==null){
           return ServiceRespose.createByErrorMeg("用户未登录");
       }
       return iShippingService.update(user.getId(),shipping);
    }
    @RequestMapping("select.do")
    @ResponseBody
    public ServiceRespose select(HttpSession session,Integer shippingId){
       User user= (User) session.getAttribute(Const.CURRENT_USER);
       if (user==null){
           return ServiceRespose.createByErrorMeg("用户未登录");
       }
       return iShippingService.select(user.getId(),shippingId);
    }
}
