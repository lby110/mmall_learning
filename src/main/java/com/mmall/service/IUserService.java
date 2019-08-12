package com.mmall.service;

import com.mmall.common.ServiceRespose;
import com.mmall.pojo.User;

public interface IUserService {
     ServiceRespose<User> login(String username, String password);
     ServiceRespose<String> register(User user);
     ServiceRespose<String> checkValid(String str,String type);
     ServiceRespose<String> selectQuestion(String username);
     ServiceRespose<String> checkAnswer(String username,String question,String answer);
     ServiceRespose<String> forgetRestPassword(String username,String passwordNew,String forgetToken);
     ServiceRespose<String> resetPassword(String passwordOld,String passwordNew,User user );
     ServiceRespose<User> updateInfomation(User user);
     ServiceRespose<User> getInformation(Integer userId);
     ServiceRespose checkAdminRole(User user);
}
