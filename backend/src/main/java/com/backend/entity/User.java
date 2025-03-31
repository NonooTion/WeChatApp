package com.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
@Data
@TableName("user")
public class User {
    @TableId(value = "user_id",type = IdType.AUTO)
    public Integer userId;
    public String username;

    public String password;

    public String email;
    public String phone;
    public String name;
    public LocalDateTime createTime;
    public String role;
    private String imgUrl;
}
