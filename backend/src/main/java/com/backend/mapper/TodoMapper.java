package com.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.entity.Todo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface TodoMapper extends BaseMapper<Todo> {

    /**
     * 根据用户ID查询所有Todo
     *
     * @param userId 用户ID
     * @return Todo列表
     */
    @Select("SELECT * FROM todo WHERE user_id = #{userId}")
    List<Todo> selectListByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户ID和条件查询Todo
     *
     * @param userId 用户ID
     * @param content 日程内容
     * @param priority 优先级
     * @return Todo列表
     */
    @Select("SELECT * FROM todo WHERE user_id = #{userId} " +
            "AND (content LIKE CONCAT('%', #{content}, '%') OR #{content} IS NULL) " +
            "AND (priority = #{priority} OR #{priority} IS NULL)")
    List<Todo> selectListByUserIdAndConditions(
            @Param("userId") Integer userId,
            @Param("content") String content,
            @Param("priority") Integer priority);
}