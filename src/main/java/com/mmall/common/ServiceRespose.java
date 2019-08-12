package com.mmall.common;

import jdk.internal.dynalink.beans.StaticClass;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候如果死null对象key也会消失
public class ServiceRespose<T> implements Serializable {

    private int status;
    private String  msg;
    private T data;
    private ServiceRespose(int status){
        this.status=status;
    }
    private ServiceRespose(int status,T data){
        this.status=status;
        this.data=data;
    }
    private ServiceRespose(int status,String  msg){
        this.status=status;
        this.msg=msg;
    }
    private ServiceRespose(int status,String  msg,T data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }

    @JsonIgnore
    //使他不在json序列化结果当中
    public boolean isSuccess(){
        return this.status==ResposeCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static <T> ServiceRespose<T> createBySuccess(){
        return new ServiceRespose<T>(ResposeCode.SUCCESS.getCode());
    }
    public static <T> ServiceRespose<T> createBySuccess(T data){
        return new ServiceRespose<T>(ResposeCode.SUCCESS.getCode(),data);
    }
    public static <T> ServiceRespose<T> createBySuccessMsg(String msg){
        return new ServiceRespose<T>(ResposeCode.SUCCESS.getCode(),msg);
    }
    public static <T> ServiceRespose<T> createBySuccess(String msg,T data){
        return new ServiceRespose<T>(ResposeCode.SUCCESS.getCode(),msg,data);
    }
    public static <T> ServiceRespose<T> createByError(){
        return new ServiceRespose<T>(ResposeCode.ERROR.getCode(),ResposeCode.ERROR.getDesc());
    }
    public static  <T> ServiceRespose<T> createByErrorMeg(String errorMsg){
        return new ServiceRespose<T>(ResposeCode.ERROR.getCode(),errorMsg);
    }
    public static <T> ServiceRespose<T> createByErrorCodeMsg(int errorCode,String errorMsg){
        return new ServiceRespose<T>(errorCode,errorMsg);
    }
}
