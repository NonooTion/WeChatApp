package com.backend.mapper;

import com.backend.entity.Course;
import com.backend.entity.Result;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

}
