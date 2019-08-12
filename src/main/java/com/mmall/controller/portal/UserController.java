package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResposeCode;
import com.mmall.common.ServiceRespose;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * By user lby
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;
    /**
     * 登陆功能开发
     * @param username:用户名
     * @param password:密码
     * @param session:信息
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<User> login(String username, String password, HttpSession session){
        ServiceRespose<User> respose=iUserService.login(username,password);
        if (respose.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,respose.getData());
        }
        return respose;
    }

    /**
     *登出功能开发;把session中的user属性清除
     * @param session
     * @return
     */
    @RequestMapping(value = "loginout.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<User> loginOut(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceRespose.createBySuccess();
    }

    /**
     * 注册功能开发
     * @param user:对象
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 校验用户填写的信息及时反馈给用户
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_Valid.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<String> checkValid(String str,String type){
      return  iUserService.checkValid(str,type);
    }

    /**
     *实现获取用户的个人信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<User> getUserInfo(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user!=null){
            return ServiceRespose.createBySuccess(user);
        }
        return ServiceRespose.createByErrorMeg("用户未登录,无法获取用户信息");
    }

    /**
     * 通过用户名获取密码
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<String> forgetGetQuestion(String username){
       return iUserService.selectQuestion(username);
    }

    /**
     * 校验问题的答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_get_answer.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<String> forgetCheckAnswer(String username,String question,String answer){
       return iUserService.checkAnswer(username,question,answer);
    }

    /**
     * 忘记密码重置密码*
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_rest_password.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetRestPassword(username,passwordNew,forgetToken);
    }

    /**
     * 状态下用旧密码修改密码
     * @param passwordOld
     * @param passwordNew
     * @param session
     * @return
     */
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<String> resetPassword(String passwordOld,String passwordNew, HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    /**
     *更新用户的个人信息
     * @param session
     * @return
     */
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<User> updateInformation(HttpSession session,User user){
       User CurrentUser= (User) session.getAttribute(Const.CURRENT_USER);
        if (CurrentUser==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        user.setId(CurrentUser.getId());
        user.setUsername(CurrentUser.getUsername());
       ServiceRespose<User> respose= iUserService.updateInfomation(user);
        if (respose.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,respose.getData());
        }
        return respose;
    }

    /**
     * 如果调用这个接口未登录,需强制登陆
     * @param session
     * @return
     */
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody//该注解功能是返回一个json序列化的对象
    public ServiceRespose<User> get_Information(HttpSession session){
        User CurrentUser= (User) session.getAttribute(Const.CURRENT_USER);
        if (CurrentUser==null){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.NEED_LOGIN.getCode(),"未登录,需要强制登陆status=10");
        }
        return iUserService.getInformation(CurrentUser.getId());
    }
}
