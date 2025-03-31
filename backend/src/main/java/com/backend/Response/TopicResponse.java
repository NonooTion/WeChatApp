package com.backend.Response;

import com.backend.entity.Topic;
import lombok.Data;

@Data
public class TopicResponse {
    private Integer topicId;
    private String title;
    private String content;
    private Boolean isAnonymous;
    private Integer userId;
    private Integer likeCount;
    private Integer commentCount;
    private String createTime;
    private String name;
    private String imgUrl;
} 