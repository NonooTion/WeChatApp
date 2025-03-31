package com.backend.service;

import com.backend.entity.Code;
import com.backend.entity.Result;
import com.backend.entity.Topic;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TopicService {
    
    /**
     * 获取话题列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param type 话题类型（normal/anonymous）
     * @param keyword 搜索关键词
     * @return 话题列表
     */
    Result<?> getTopicList(Integer pageNum, Integer pageSize, String type, String keyword);

    /**
     * 获取我的话题列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 我的话题列表
     */
    Result<?> getMyTopics(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * 删除话题
     * @param topicId 话题ID
     * @return 是否成功
     */
    Result<?> deleteTopic(Integer topicId);

    /**
     * 发布话题
     * @param topic 话题信息
     * @return 发布的话题
     */
    Result<?> publishTopic(Topic topic);

    /**
     * 获取话题详情
     * @param id 话题ID
     * @return 话题详情
     */
    Result<?> getTopicDetail(Integer id);

    /**
     * 点赞话题
     * @param topicId 话题ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Result<?> likeTopic(Integer topicId, Integer userId);

    /**
     * 评论话题
     * @param topicId 话题ID
     * @param userId 用户ID
     * @param content 评论内容
     * @return 是否成功
     */
    Result<?> commentTopic(Integer topicId, Integer userId, String content);
} 