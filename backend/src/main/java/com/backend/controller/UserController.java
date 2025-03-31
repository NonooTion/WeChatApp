package com.backend.controller;

import com.backend.entity.Code;
import com.backend.entity.Result;
import com.backend.service.UserService;
import com.backend.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    JWTUtil jwtUtil;
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {

        String usernameOrPhone = loginRequest.get("usernameOrPhone");
        String password = loginRequest.get("password");
        Result<Map<String, Object>> login = userService.login(usernameOrPhone, password);
        return login;
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest) {
            String username=registerRequest.get("username");
            String phone=registerRequest.get("phone");
            String password=registerRequest.get("password");
            Result<Map<String, Object>> register = userService.register(username, phone, password);
            return register;
    }

    @PostMapping("/forget-password")
    public Result<Map<String,Object>> forgetPassword(@RequestBody Map<String, String> registerRequest)
    {
        String username=registerRequest.get("username");
        String newPassword=registerRequest.get("newPassword");
        Result<Map<String, Object>> mapResult = userService.forgetPassword(username, newPassword);
        return mapResult;
    }

    @GetMapping("/get-by-user-id")
    public Result<?> getUserByUserId(@RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization))
        {
            return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        return userService.getUserByUserId(userId);
    }

    @PutMapping("/update")
    public Result<?> updateUser(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization))
        {
            return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
        }
        Integer userIdFromToken = jwtUtil.getUserIdFromToken(authorization);
        return userService.updateTodo(userIdFromToken, name, email, phone);
    }

    @PostMapping("/upload/avatar")
    public Result<Map<String, Object>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authorization) {
        if (jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        return userService.uploadAvatar(userId, file);
    }
}
