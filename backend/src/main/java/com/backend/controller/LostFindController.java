package com.backend.controller;

import com.backend.entity.Code;
import com.backend.entity.LostFind;
import com.backend.entity.Result;
import com.backend.service.LostFindService;
import com.backend.util.JWTUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/lost-find")
public class LostFindController {
    
    @Autowired
    private LostFindService lostFindService;
    
    @Autowired
    private JWTUtil jwtUtil;
    
    @PostMapping("/publish")
    public Result<?> publish(@RequestBody LostFind lostFind, @RequestHeader("Authorization") String authorization) {
        if (jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        System.out.println(lostFind);
        lostFind.setUserId(jwtUtil.getUserIdFromToken(authorization));
        return lostFindService.publish(lostFind);
    }
    
    @PutMapping("/update")
    public Result<?> update(@RequestBody LostFind lostFind, @RequestHeader("Authorization") String authorization) {
        if (jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        return lostFindService.update(lostFind);
    }
    
    @DeleteMapping("/delete/{lfId}")
    public Result<?> delete(@PathVariable Integer lfId, @RequestHeader("Authorization") String authorization) {
        if (jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        return lostFindService.delete(lfId);
    }
    
    @GetMapping("/detail/{lfId}")
    public Result<?> getDetail(@PathVariable Integer lfId) {
        return lostFindService.getDetail(lfId);
    }
    
    @GetMapping("/list")
    public Result<Page<LostFind>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {
        return lostFindService.getList(pageNum, pageSize, status, keyword, type);
    }
    
    @GetMapping("/user-list")
    public Result<Page<LostFind>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestHeader("Authorization") String authorization) {
        if (jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        return lostFindService.getUserList(userId, pageNum, pageSize, type);
    }
    
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authorization) {
        if (jwtUtil.isTokenExpired(authorization)) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "Token过期，请重新登录");
        }
        return lostFindService.uploadImage(file);
    }
} 