package com.backend.service.impl;

import com.backend.Response.CommentResponse;
import com.backend.entity.Comment;
import com.backend.entity.User;
import com.backend.mapper.CommentMapper;
import com.backend.mapper.UserMapper;
import com.backend.service.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public boolean addComment(Comment comment) {
        System.out.println(comment);
        return save(comment);
    }
    
    @Override
    public boolean deleteComment(Integer commentId) {
        return removeById(commentId);
    }
    
    @Override
    public Page<CommentResponse> getCommentsByTopicId(Integer topicId, Page<Comment> page) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getTopicId, topicId)
                .orderByDesc(Comment::getCreateTime);
        Page<Comment> commentPage = page(page, wrapper);
        
        // 转换为 CommentResponse
        List<CommentResponse> records = commentPage.getRecords().stream().map(comment -> {
            CommentResponse response = new CommentResponse();
            response.setCommentId(comment.getCommentId());
            response.setTopicId(comment.getTopicId());
            response.setUserId(comment.getUserId());
            response.setContent(comment.getContent());
            response.setCreateTime(comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // 获取用户信息
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                response.setName(user.getName());
                response.setAvatar(user.getImgUrl());
            }
            
            return response;
        }).collect(Collectors.toList());
        
        Page<CommentResponse> responsePage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }
    
    @Override
    public List<Comment> getCommentsByUserId(Integer userId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getUserId, userId)
                .orderByDesc(Comment::getCreateTime);
        return list(wrapper);
    }
} 