package com.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.entity.Topic;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
} 