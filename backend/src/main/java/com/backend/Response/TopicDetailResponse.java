package com.backend.Response;

import com.backend.entity.Topic;
import lombok.Data;

@Data
public class TopicDetailResponse {
    private Topic topic;
    private UserInfo author;
    private boolean isLiked;

    @Data
    public static class UserInfo {
        private Integer userId;
        private String name;
        private String avatar;
    }
} 