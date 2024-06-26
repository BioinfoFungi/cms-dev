package com.wangyang.pojo.authorize;

import lombok.Data;

/**
 * @author wangyang
 * @date 2021/6/11
 */
@Data
public class UserLoginParam {
    private String username;
    private String password;

    public UserLoginParam(){}

    public UserLoginParam(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
