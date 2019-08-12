package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServiceRespose;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;

    /**
     *添加购物车
     * @param session
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("add.do")
    @ResponseBody
        public ServiceRespose<CartVo> add(HttpSession session, Integer count, Integer productId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        return iCartService.add(user.getId(),count,productId);
    }

    /**
     *产品列表
     * @param session
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServiceRespose<CartVo> list(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        return iCartService.list(user.getId());
    }

    /**
     * 更新购物车
     * @param session
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServiceRespose<CartVo> update(HttpSession session,Integer count,Integer productId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        return iCartService.update(user.getId(),count,productId);
    }

    /**
     * 删除产品
     * @param session
     * @param productIds
     * @return
     */
    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServiceRespose<CartVo> deleteProduct(HttpSession session,String productIds){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        return iCartService.delete(user.getId(),productIds);
    }

    /**
     * 全选
     * @param session
     * @return
     */
    @RequestMapping("all_checked.do")
    @ResponseBody
    public ServiceRespose<CartVo> allChecked(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        return iCartService.allChecked(user.getId(),Const.Cart.CHECKED);
    }
    /**
     * 全反选
     * @param session
     * @return
     */
    @RequestMapping("all_unchecked.do")
    @ResponseBody
    public ServiceRespose<CartVo> allUnChecked(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        return iCartService.allChecked(user.getId(),Const.Cart.UN_CHECK);
    }

    /**
     * 单独选
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("one_checked.do")
    @ResponseBody
    public ServiceRespose<CartVo> OneChecked(HttpSession session,Integer productId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        return iCartService.allOneChecked(user.getId(),Const.Cart.CHECKED,productId);
    }

    /**
     * 单独反选
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("one_unchecked.do")
    @ResponseBody
    public ServiceRespose<CartVo> oneUnChecked(HttpSession session,Integer productId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        return iCartService.allOneChecked(user.getId(),Const.Cart.UN_CHECK,productId);
    }

    /**
     * 查询当前用户购物车产品的数量,有10个就显示10
     * @param session
     * @return
     */
    @RequestMapping("select_count.do")
    @ResponseBody
    public ServiceRespose<Integer> selectCount(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        return iCartService.selectCount(user.getId());
    }

}
