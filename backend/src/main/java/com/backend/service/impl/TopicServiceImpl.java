package com.backend.service.impl;

import com.backend.entity.Code;
import com.backend.entity.Comment;
import com.backend.entity.Result;
import com.backend.entity.Topic;
import com.backend.entity.User;
import com.backend.Response.TopicResponse;
import com.backend.mapper.TopicMapper;
import com.backend.mapper.UserMapper;
import com.backend.service.CommentService;
import com.backend.service.TopicService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    CommentService commentService;
    @Override
    public Result<?> getTopicList(Integer pageNum, Integer pageSize, String type, String keyword) {
        // 先查询总记录数
        LambdaQueryWrapper<Topic> countWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            countWrapper.eq(Topic::getIsAnonymous, "anonymous".equals(type));
        }
        if (StringUtils.hasText(keyword)) {
            countWrapper.like(Topic::getTitle, keyword)
                  .or()
                  .like(Topic::getContent, keyword);
        }
        long total = this.count(countWrapper);
        
        // 计算总页数
        long pages = (total + pageSize - 1) / pageSize;
        
        // 查询当前页数据
        Page<Topic> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(type)) {
            wrapper.eq(Topic::getIsAnonymous, "anonymous".equals(type));
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Topic::getTitle, keyword)
                  .or()
                  .like(Topic::getContent, keyword);
        }
        
        wrapper.orderByDesc(Topic::getCreateTime);
        Page<Topic> topicPage = this.page(page, wrapper);
        
        List<TopicResponse> responseList = new ArrayList<>();
        for (Topic topic : topicPage.getRecords()) {
            TopicResponse response = new TopicResponse();
            response.setTopicId(topic.getTopicId());
            response.setTitle(topic.getTitle());
            response.setContent(topic.getContent());
            response.setIsAnonymous(topic.getIsAnonymous());
            response.setUserId(topic.getUserId());
            response.setLikeCount(topic.getLikeCount());
            response.setCommentCount(topic.getCommentCount());
            response.setCreateTime(topic.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // 获取用户信息
            User user = userMapper.selectById(topic.getUserId());
            if (user != null) {
                response.setName(user.getName());
                response.setImgUrl(user.getImgUrl());
            }
            
            responseList.add(response);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", responseList);
        result.put("total", total);
        result.put("size", pageSize);
        result.put("current", pageNum);
        result.put("pages", pages);
        
        return Result.success(result);
    }

    @Override
    @Transactional
    public Result<?> publishTopic(Topic topic) {
        try {
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            topic.setCreateTime(now);
            topic.setUpdateTime(now);
            
            // 设置初始点赞数和评论数
            topic.setLikeCount(0);
            topic.setCommentCount(0);
            
            if (this.save(topic)) {
                return Result.success(topic);
            } else {
                return Result.fail(Code.CODE_SERVER_ERROR, "发布话题失败");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "发布话题异常: " + e.getMessage());
        }
    }

    @Override
    public Result<?> getTopicDetail(Integer id) {
        Topic topic = this.getById(id);
        if (topic == null) {
            return Result.fail(Code.CODE_NOT_FOUND, "未找到该话题");
        }
        
        TopicResponse response = new TopicResponse();
        response.setTopicId(topic.getTopicId());
        response.setTitle(topic.getTitle());
        response.setContent(topic.getContent());
        response.setIsAnonymous(topic.getIsAnonymous());
        response.setUserId(topic.getUserId());
        response.setLikeCount(topic.getLikeCount());
        response.setCommentCount(topic.getCommentCount());
        response.setCreateTime(topic.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 获取用户信息
        User user = userMapper.selectById(topic.getUserId());
        if (user != null) {
            response.setName(user.getName());
            response.setImgUrl(user.getImgUrl());
        }
        
        return Result.success(response);
    }

    @Override
    @Transactional
    public Result<?> likeTopic(Integer topicId, Integer userId) {
        try {
            Topic topic = this.getById(topicId);
            if (topic == null) {
                return Result.fail(Code.CODE_NOT_FOUND, "话题不存在");
            }
            
            topic.setLikeCount(topic.getLikeCount() + 1);
            this.updateById(topic);
            return Result.success("点赞成功");
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "点赞失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<?> commentTopic(Integer topicId, Integer userId, String content) {
        try {
            Topic topic = this.getById(topicId);
            if (topic == null) {
                return Result.fail(Code.CODE_NOT_FOUND, "话题不存在");
            }
            
            topic.setCommentCount(topic.getCommentCount() + 1);
            this.updateById(topic);
            Comment comment=new Comment();
            comment.setUserId(userId);
            comment.setTopicId(topicId);
            comment.setContent(content);
            comment.setCreateTime(LocalDateTime.now());
            commentService.addComment(comment);
            return Result.success("评论成功");
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "评论失败: " + e.getMessage());
        }
    }

    @Override
    public Result<?> getMyTopics(Integer userId, Integer pageNum, Integer pageSize) {
        // 先查询总记录数
        LambdaQueryWrapper<Topic> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Topic::getUserId, userId);
        long total = this.count(countWrapper);
        
        // 计算总页数
        long pages = (total + pageSize - 1) / pageSize;
        
        // 查询当前页数据
        Page<Topic> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Topic::getUserId, userId)
               .orderByDesc(Topic::getCreateTime);
        Page<Topic> topicPage = this.page(page, wrapper);
        
        List<TopicResponse> responseList = new ArrayList<>();
        for (Topic topic : topicPage.getRecords()) {
            TopicResponse response = new TopicResponse();
            response.setTopicId(topic.getTopicId());
            response.setTitle(topic.getTitle());
            response.setContent(topic.getContent());
            response.setIsAnonymous(topic.getIsAnonymous());
            response.setUserId(topic.getUserId());
            response.setLikeCount(topic.getLikeCount());
            response.setCommentCount(topic.getCommentCount());
            response.setCreateTime(topic.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // 获取用户信息
            User user = userMapper.selectById(topic.getUserId());
            if (user != null) {
                response.setName(user.getName());
                response.setImgUrl(user.getImgUrl());
            }
            
            responseList.add(response);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", responseList);
        result.put("total", total);
        result.put("size", pageSize);
        result.put("current", pageNum);
        result.put("pages", pages);
        
        return Result.success(result);
    }

    @Override
    @Transactional
    public Result<?> deleteTopic(Integer topicId) {
        try {
            Topic topic = this.getById(topicId);
            if (topic == null) {
                return Result.fail(Code.CODE_NOT_FOUND, "话题不存在");
            }
            
            if (this.removeById(topicId)) {
                return Result.success("删除成功");
            } else {
                return Result.fail(Code.CODE_SERVER_ERROR, "删除失败");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "删除异常: " + e.getMessage());
        }
    }
} 