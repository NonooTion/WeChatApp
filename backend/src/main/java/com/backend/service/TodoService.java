package com.backend.service;

import com.backend.entity.Result;
import com.backend.entity.Code;
import com.backend.entity.Todo;
import com.backend.mapper.TodoMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class TodoService {

    @Autowired
    private TodoMapper todoMapper;

    /**
     * 添加日程表
     *
     * @param userId 用户ID
     * @param content 日程内容
     * @param priority 优先级
     * @param remindTime 提醒时间
     * @return 操作结果
     */
    public Result<?> addTodo(Integer userId, String content, String priority, LocalDateTime remindTime) {
        try {
            Todo todo = new Todo();
            todo.setUserId(userId);
            todo.setContent(content);
            todo.setPriority(priority);
            todo.setRemindTime(remindTime);
            todo.setStatus("未完成");
            todo.setCreateTime(LocalDateTime.now());

            int rows = todoMapper.insert(todo);
            if (rows > 0) {
                return Result.success("日程添加成功");
            } else {
                return Result.fail(Code.CODE_SERVER_ERROR, "日程添加失败");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "日程添加异常: " + e.getMessage());
        }
    }

    /**
     * 修改日程表
     *
     * @param todoId 日程ID
     * @param content 日程内容
     * @param priority 优先级
     * @param remindTime 提醒时间
     * @param status 状态
     * @return 操作结果
     */
    public Result<?> updateTodo(Integer todoId, String content, String priority, LocalDateTime remindTime, String status) {
        try {
            Todo todo = todoMapper.selectById(todoId);
            if (todo == null) {
                return Result.fail(Code.CODE_NOT_FOUND, "日程不存在");
            }

            if (content != null) {
                todo.setContent(content);
            }
            if (priority != null) {
                todo.setPriority(priority);
            }
            if (remindTime != null) {
                todo.setRemindTime(remindTime);
            }
            if (status != null) {
                todo.setStatus(status);
            }

            int rows = todoMapper.updateById(todo);
            if (rows > 0) {
                return Result.success("日程修改成功");
            } else {
                return Result.fail(Code.CODE_SERVER_ERROR, "日程修改失败");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "日程修改异常: " + e.getMessage());
        }
    }

    /**
     * 删除日程表
     *
     * @param todoId 日程ID
     * @return 操作结果
     */
    public Result<?> deleteTodo(Integer todoId) {
        try {
            Todo todo = todoMapper.selectById(todoId);
            if (todo == null) {
                return Result.fail(Code.CODE_NOT_FOUND, "日程不存在");
            }

            int rows = todoMapper.deleteById(todoId);
            if (rows > 0) {
                return Result.success("日程删除成功");
            } else {
                return Result.fail(Code.CODE_SERVER_ERROR, "日程删除失败");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "日程删除异常: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID搜索所有Todo
     *
     * @param userId 用户ID
     * @return Todo列表
     */
    public Result<?> getTodosByUserId(Integer userId) {
        try {
            List<Todo> todos = todoMapper.selectList(new QueryWrapper<Todo>().eq("user_id",userId));
            if (todos != null) {
                return Result.success(todos);
            } else {
                return Result.fail(Code.CODE_NOT_FOUND, "未找到该用户的日程");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "查询日程异常: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID和条件查询Todo
     *
     * @param userId 用户ID
     * @param content 日程内容
     * @param priority 优先级
     * @return Todo列表
     */
    public Result<?> getTodosByUserIdAndConditions(Integer userId, String content, Integer priority) {
        try {
            List<Todo> todos = todoMapper.selectListByUserIdAndConditions(userId, content, priority);
            if (todos != null) {
                return Result.success(todos);
            } else {
                return Result.fail(Code.CODE_NOT_FOUND, "未找到符合条件的日程");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "查询日程异常: " + e.getMessage());
        }
    }
}