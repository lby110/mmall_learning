package com.mmall.common;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER="currentUser";
    public static final String EMAIL="emial";
    public static final String USERNAME="username";

    public interface orderBy{
        Set<String> PRICE_ASC_DESC= Sets.newHashSet("price_desc","price_asc");
    }

    public interface role{
        int ROLE_CUSTOMER=0;//普通用户
        int ROLE_ADMIN=1;//管理员
    }

    public interface Cart{
        Integer CHECKED=1;//购物车选中状态
        Integer UN_CHECK=0;//购物车未选中状态
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";

    }


    public enum ProductStatusEnum{
        ON_SALE(1,"在售")
        ;
        private int code;
        private String msg;

        ProductStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
