package com.backend.Response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LostFindResponse {
    private Integer lfId;
    private Integer userId;
    private String title;
    private String content;
    private String location;
    private String status;
    private String itemImgUrl;
    private LocalDateTime createTime;
    private String name;
    private String phone;
    private String email;
    private String type;
} 