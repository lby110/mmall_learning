package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServiceRespose;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登陆模块(前后台可共用,是否是管理员在controller层进行判断即可)
     * @param username
     * @param password
     * @return
     * 逻辑分析: 通过用户or管理员登陆的username跟password进行查询
     *          1.查询用户名,如果不存在则返回提示信息用户名不存在
     *          2.查询密码(密码先调用MD5加密后传入),若存在返回提示信息密码错误
     *          3.用户名跟密码都无误以后,及时把user对象里面的password置空
     *          4.返回给前端提示信息登陆成功,并把相对应的user对象的信息返回
     */
    @Override
    public ServiceRespose<User> login(String username, String password) {
       int resultCount= userMapper.selectByUsername(username);
        if (resultCount==0){
            return ServiceRespose.createByErrorMeg("用户名不存在");
        }
        //密码登陆MD5
        String md5Password=MD5Util.MD5EncodeUtf8(password);

        User user=userMapper.selectLogin(username,md5Password);
        if (user==null){
            return ServiceRespose.createByErrorMeg("用户密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceRespose.createBySuccess("登陆成功",user);
    }

    /**
     * 注册用户
     * @param user
     * @return
     * 逻辑分析:1.用户注册我们需要进行对用户名和邮箱校验,查询数据库中是否已经存在了用户名和邮箱
     *         2.调用校验的方法来完成
     */
    public ServiceRespose<String> register(User user){
       ServiceRespose<String> validRespose =this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validRespose.isSuccess()){//逻辑分析:理解!!!一定要理解这里!!
            /*如果校验没有通过,我们会发现
            public boolean isSuccess(){
                return this.status==ResposeCode.SUCCESS.getCode();
            }
            只有当status==0的时候才会返回true,其余的都会返回false.就像我们这里校验不通过 那么isSuccess的返回时false.取非
            让代码执行if里面的语句  所以,邮箱的isSuccess校验取非也是这个意思
            */
            return validRespose;
        }
         validRespose=this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validRespose.isSuccess()){//逻辑分析,issuccess是校验通过,取非就是不通过那么返回validRespose
            return validRespose;
        }
        user.setRole(Const.role.ROLE_CUSTOMER);
        //md5加密密码
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //插入到数据库中
        int resultCount=userMapper.insert(user);
        if (resultCount==0){
            return ServiceRespose.createByErrorMeg("注册失败");
        }
        return ServiceRespose.createBySuccess("注册成功");
    }

    /**
     * 用户名and邮箱的校验
     * @param str:校验数据
     * @param type:校验类型:username or email
     * @return
     * 逻辑分析:如果校验类型不为空,开始校验;如果校验类型为空则返回提示信息"参数错误"
     *          1.如果校验类型是username
     *          2.通过用户名查询数据库,如果大于0,说明用户已经存在.返回提示信息"用户名已存在"
     *          3.如果校验类型是email,校验邮箱
     *          4.通过email查询数据库
     *          5.如果查询结果大于1则返回提示信息"邮箱已经存在"
     *          6.如果都通过则返回提示信息"校验成功,用户名和邮箱可用"
     */
    public ServiceRespose<String> checkValid(String str,String type){
        if (StringUtils.isNoneBlank(type)){
            //开始校验
            if (Const.USERNAME.equals(type)){
                int resultCount= userMapper.selectByUsername(str);
                if (resultCount>0){
                    return ServiceRespose.createByErrorMeg("用户名已经存在");
                }
            }
            if (Const.EMAIL.equals(type)){
               int resultCount=userMapper.selectByEmail(str);
                if (resultCount>0){
                    return ServiceRespose.createByErrorMeg("邮箱已经存在");
                }
            }
        }else {
            return ServiceRespose.createByErrorMeg("参数错误");
        }
        return ServiceRespose.createBySuccess("校验成功,用户名和邮箱可用");
    }

    /**
     * 找回密码问题查询
     * @param username
     * @return
     *逻辑分析:通过传进来的username来查询密码.查询密码之前校验一下用户名是否存在
     */
    public ServiceRespose<String> selectQuestion(String username){
      ServiceRespose validRespose =  this.checkValid(username,Const.USERNAME);
      if (validRespose.isSuccess()){
          //校验逻辑如注册一样分析
          return ServiceRespose.createByErrorMeg("用户不存在");
      }
      String question=userMapper.selectQuestionByUsername(username);
    if (StringUtils.isNotBlank(question)){
        return ServiceRespose.createBySuccessMsg(question);
    }
    return ServiceRespose.createByErrorMeg("找回密码问题为空");
    }

    /**
     * 校验问题的答案
     * @param username
     * @param question
     * @param answer
     * @return
     * 逻辑分析:通过用户名,问题,答案查询数据库,如果返回的结果大于0,则说明三个参数跟数据库里的一样.
     *          我们就造一个token返回用来;否则返回提示"问题的答案错误"
     */
    public ServiceRespose<String> checkAnswer(String username,String question,String answer){
       int resultCount = userMapper.checkAnswer(username,question,answer);
       if (resultCount>0){
           //如果大于0,那么说明用户名\问题\答案都是一样的
           String forgetToken=UUID.randomUUID().toString();
           TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
           //System.out.println(forgetToken);
           return ServiceRespose.createBySuccess(forgetToken);
       }
       return ServiceRespose.createByErrorMeg("问题的答案错误");
    }

    /**
     * 忘记密码重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     * 逻辑分析: 1.重置密码的时候有一个token传进来,首先就是判断token.如果为空则直接返回提示"参数token错误"
     *          2.校验username是否存在
     *          3.获取缓存中的token,如果缓存中没有token则返回提示"token无效或已经过期",与传进来的token进行对比.如果完全一致则修改密码.
     *          如果不一样则返回提示"token错误,请重新获取token"
     *          4.把新密码进行MD5加密
     *          5.update数据库中的密码
     *          6.返回提示"修改密码成功"
     */
    public ServiceRespose<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            return ServiceRespose.createByErrorMeg("参数token错误");
        }
        //校验username
        ServiceRespose validRespose =  this.checkValid(username,Const.USERNAME);
        if (validRespose.isSuccess()){//如果是1表示用户不存在
            return ServiceRespose.createByErrorMeg("用户不存在");
        }
        //缓存中获取token
       String token= TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)){
            return ServiceRespose.createByErrorMeg("token无效或者已经过期");
        }
        if (StringUtils.equals(forgetToken,token)){
         String md5PasswordNew =  MD5Util.MD5EncodeUtf8(passwordNew);
        int resultCount= userMapper.updatePasswordNewByUsername(username,md5PasswordNew);
        if (resultCount>0){
            return ServiceRespose.createBySuccessMsg("修改密码成功");
        }
        }else {
            return ServiceRespose.createByErrorMeg("token错误,请重新获取重置密码token");
        }
        return ServiceRespose.createByErrorMeg("修改密码失败");
    }

    /**
     * 登陆状态下重置密码
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     * 逻辑分析: 1.为了防止越权,我们通过用户id和旧密码来防止越权
     *          2.如果返回结果为"0"则返回提示"旧密码错误,请重新输入"
     *          3.把我们的新密码进行md5加密注入到user对象
     *          4.update数据库
     *          5.如果update返回结果大于"0"则返回提示消息"修改密码成功"
     *          否则返回提示信息"修改密码失败"
     */
    public ServiceRespose<String> resetPassword(String passwordOld,String passwordNew,User user ){
        //防止横向越权,我们校验一下旧密码
       int  resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (resultCount==0){
            return ServiceRespose.createByErrorMeg("旧密码错误,请重新输入");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
       int updateCount= userMapper.updateByPrimaryKeySelective(user);
       if (updateCount>0){
           return ServiceRespose.createBySuccessMsg("修改密码成功");
       }
       return ServiceRespose.createByErrorMeg("修改密码失败");
    }

    /**
     * 更新个人信息
     * @param user
     * @return
     * 逻辑分析: 1.明确登陆状态下username不需要被更新
     *          2.进行email校验.如果email存在则返回提示消息"邮箱已经存在,请更换email"
     *          3.创建user对象.注入用户修改后的信息
     *          4.update数据库
     *          5.返回结果大于"0" 则返回提示消息"更新个人信息成功"
     *          否则返回"更新个人信息失败"
     */
    public ServiceRespose<User> updateInfomation(User user){
        //username不需要被更新
        //email需要进行校验.如果存在了那么返回错误给前端
        int  resultCount=userMapper.checkEmail(user.getEmail(),user.getId());
        if (resultCount>0){
            return ServiceRespose.createByErrorMeg("email已存在,请更换email");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount>0){
            return ServiceRespose.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServiceRespose.createBySuccessMsg("更新个人信息失败");
    }

    /**
     * 获取个人信息
     * @param userId
     * @return
     *逻辑分析:通过用户id查询个人信息.如果user==null返回提示消息即可
     */
    public ServiceRespose<User> getInformation(Integer userId){
       User user= userMapper.selectByPrimaryKey(userId);
       if (user==null){
           return ServiceRespose.createBySuccessMsg("找不到当前用户");
       }
       return ServiceRespose.createBySuccess(user);
    }

    /**
     * 校验是否是管理员
     * @param user
     * @return
     * 逻辑分析:只需要判断user不为null并且user对象的Role的值跟我们设置的静态ROLE_ADMIN一样就可以确定用户是管理员
     */
    public ServiceRespose checkAdminRole(User user){
        if (user!=null&&user.getRole().intValue()==Const.role.ROLE_ADMIN){
            return ServiceRespose.createBySuccess();
        }
        return ServiceRespose.createByError();
    }
}
