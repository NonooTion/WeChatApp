package com.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("topic")
public class Topic {
    @TableId(value = "topic_id", type = IdType.AUTO)
    private Integer topicId;
    
    private String title;
    
    private String content;
    
    private Boolean isAnonymous;
    
    private Integer userId;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 