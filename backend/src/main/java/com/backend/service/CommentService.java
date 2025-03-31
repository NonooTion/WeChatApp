package com.backend.service;

import com.backend.Response.CommentResponse;
import com.backend.entity.Comment;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CommentService extends IService<Comment> {
    // 添加评论
    boolean addComment(Comment comment);
    
    // 删除评论
    boolean deleteComment(Integer commentId);
    
    // 获取话题的所有评论
    Page<CommentResponse> getCommentsByTopicId(Integer topicId, Page<Comment> page);
    
    // 获取用户的所有评论
    List<Comment> getCommentsByUserId(Integer userId);
} 