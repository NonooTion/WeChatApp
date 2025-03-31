package com.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lost_found")
public class LostFind {
    @TableId(type = IdType.AUTO)
    private Integer lfId;
    private Integer userId;
    private String title;
    private String content;
    private String location;
    private String status;
    private String itemImgUrl;
    private LocalDateTime createTime;
    private String type;
} 