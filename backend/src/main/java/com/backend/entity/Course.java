package com.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {
    @TableId(value = "course_id",type = IdType.AUTO)
    private Integer courseId;
    private Integer userId;
    private String courseName;
    private String day;
    private String time;
    private String classroom;
    private LocalDateTime createTime;
}
