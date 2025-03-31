package com.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("topic_comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer commentId;
    private Integer topicId;
    private Integer userId;
    private String content;
    private LocalDateTime createTime;
} 