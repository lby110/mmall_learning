package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResposeCode;
import com.mmall.common.ServiceRespose;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     *添加产品类型id
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceRespose addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            //增加我们处理分类的逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        }else {
            return ServiceRespose.createByErrorMeg("无权限操作,需要管理员权限");
        }
    }

    /**
     *更新商品类型
     * @param session
     * @param categoryName
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceRespose setCategoryName(HttpSession session,String categoryName,Integer categoryId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            //处理分类的逻辑
          return  iCategoryService.updateCategory(categoryName,categoryId);
        }else {
            return ServiceRespose.createByErrorMeg("无权限操作,需要管理员权限");
        }
    }

    /**
     * 根据传的id获取子节点的信息,平级获取
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServiceRespose getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServiceRespose.createByErrorMeg("无权限操作,需要管理员权限");
        }
    }

    /**
     *查询当前节点的id,和递归子节点的id
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceRespose getCategoryAndDeepChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
           return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else {
            return ServiceRespose.createByErrorMeg("无权限操作,需要管理员权限");
        }
    }
}
