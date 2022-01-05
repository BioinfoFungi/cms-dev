package com.wangyang.pojo.vo;

import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.CommentDto;

public class CommentVo extends CommentDto {
    private String content;
    private User user;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
