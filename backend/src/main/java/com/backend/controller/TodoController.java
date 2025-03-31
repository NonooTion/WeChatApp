package com.backend.controller;

import com.backend.entity.Code;
import com.backend.entity.Result;
import com.backend.entity.Todo;
import com.backend.service.TodoService;
import com.backend.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/todo")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private JWTUtil jwtUtil;


    @PostMapping("/add")
    public Result<?> addTodo(
            @RequestBody Todo todo,
            @RequestHeader("Authorization") String authorization) {
            if(jwtUtil.isTokenExpired(authorization))
            {
                return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
            }
            Integer userId = jwtUtil.getUserIdFromToken(authorization);
            return todoService.addTodo(userId, todo.getContent(), todo.getPriority(), todo.getRemindTime());
    }

    /**
     * 修改日程表
     *
     * @param todoId      日程ID
     * @param content     日程内容
     * @param priority    优先级
     * @param remindTime  提醒时间
     * @param status      状态
     * @param authorization 请求头中的Authorization字段
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<?> updateTodo(
            @RequestParam Integer todoId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) LocalDateTime remindTime,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String authorization) {
            if(jwtUtil.isTokenExpired(authorization))
            {
                return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
            }
            return todoService.updateTodo(todoId, content, priority, remindTime, status);
    }


    @DeleteMapping("/delete")
    public Result<?> deleteTodo(
            @RequestBody Todo todo,
            @RequestHeader("Authorization") String authorization) {
            if(jwtUtil.isTokenExpired(authorization))
            {
                return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
            }
            return todoService.deleteTodo(todo.getTodoId());
    }

    /**
     * 根据用户ID搜索所有Todo
     *
     * @param authorization 请求头中的Authorization字段
     * @return Todo列表
     */
    @GetMapping("/get-by-user-id")
    public Result<?> getTodosByUserId(@RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization))
        {
            return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        return todoService.getTodosByUserId(userId);
    }

    /**
     * 根据用户ID和条件查询Todo
     *
     * @param content     日程内容
     * @param priority    优先级
     * @param authorization 请求头中的Authorization字段
     * @return Todo列表
     */
    @GetMapping("/get-by-conditions")
    public Result<?> getTodosByUserIdAndConditions(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Integer priority,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization))
        {
            return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        return todoService.getTodosByUserIdAndConditions(userId, content, priority);
    }

}