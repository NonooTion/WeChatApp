package com.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("todo")
public class Todo {
    @TableId(value = "todo_id",type = IdType.AUTO)
    public Integer todoId;
    public Integer userId;
    public String content;
    public String priority;
    public LocalDateTime remindTime;
    public String status;
    public LocalDateTime createTime;

}
