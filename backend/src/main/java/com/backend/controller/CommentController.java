package com.backend.controller;

import com.backend.Response.CommentResponse;
import com.backend.Response.Result;
import com.backend.entity.Code;
import com.backend.entity.Comment;
import com.backend.service.CommentService;
import com.backend.util.JWTUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private JWTUtil jwtUtil;
    
    @PostMapping("/add")
    public Result<Boolean> addComment(
            @RequestBody Comment comment,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization)) {
            return Result.error("Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        comment.setUserId(userId);
        return Result.success(commentService.addComment(comment));
    }
    
    @DeleteMapping("/delete/{commentId}")
    public Result<Boolean> deleteComment(
            @PathVariable Integer commentId,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization)) {
            return Result.error("Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        // TODO: 验证评论是否属于当前用户
        return Result.success(commentService.deleteComment(commentId));
    }
    
    @GetMapping("/topic/{topicId}")
    public Result<Page<CommentResponse>> getCommentsByTopicId(
            @PathVariable Integer topicId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return Result.success(commentService.getCommentsByTopicId(topicId, page));
    }
    
    @GetMapping("/user/{userId}")
    public Result<List<Comment>> getCommentsByUserId(
            @PathVariable Integer userId,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization)) {
            return Result.error("Token过期，请重新登录");
        }
        Integer currentUserId = jwtUtil.getUserIdFromToken(authorization);
        // 只允许用户查看自己的评论
        if (!currentUserId.equals(userId)) {
            return Result.error("无权查看其他用户的评论");
        }
        return Result.success(commentService.getCommentsByUserId(userId));
    }
} 