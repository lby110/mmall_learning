package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServiceRespose;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user/")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    /**
     * 管理员登陆信息
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<User> login(String username, String password, HttpSession session){
      ServiceRespose<User> respose= iUserService.login(username,password);
      if (respose.isSuccess()){
          User user=respose.getData();
          if (user.getRole()== Const.role.ROLE_ADMIN){
              session.setAttribute(Const.CURRENT_USER,user);//把登陆查询后的user对象存进session中,用来显示页面上一些个人信息
              return respose;
          }else {
              return ServiceRespose.createByErrorMeg("该用户不是管理员,无法登录");
          }
      }
        return respose;
    }
}
