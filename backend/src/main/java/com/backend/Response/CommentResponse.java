package com.backend.Response;

import com.backend.entity.Comment;
import lombok.Data;

@Data
public class CommentResponse {
    private Integer commentId;
    private Integer topicId;
    private Integer userId;
    private String content;
    private String createTime;
    private String name;
    private String avatar;
} 