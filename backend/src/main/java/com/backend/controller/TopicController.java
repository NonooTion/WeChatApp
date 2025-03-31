package com.backend.controller;

import com.backend.entity.Code;
import com.backend.entity.Result;
import com.backend.entity.Topic;
import com.backend.Response.TopicResponse;
import com.backend.service.TopicService;
import com.backend.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topic")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/list")
    public Result<?> getTopicList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        return topicService.getTopicList(pageNum, pageSize, type, keyword);
    }

    @PostMapping("/publish")
    public Result<?> publishTopic(
            @RequestBody Topic topic,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        topic.setUserId(userId);
        return topicService.publishTopic(topic);
    }

    @GetMapping("/detail/{id}")
    public Result<?> getTopicDetail(@PathVariable Integer id) {
        return topicService.getTopicDetail(id);
    }

    @PostMapping("/like/{id}")
    public Result<?> likeTopic(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        return topicService.likeTopic(id, userId);
    }

    @PostMapping("/comment/{id}")
    public Result<?> commentTopic(
            @PathVariable Integer id,
            @RequestBody String content,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        return topicService.commentTopic(id, userId, content);
    }

    @GetMapping("/my")
    public Result<?> getMyTopics(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        return topicService.getMyTopics(userId, pageNum, pageSize);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteTopic(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        // TODO: 验证话题是否属于当前用户
        return topicService.deleteTopic(id);
    }
} 