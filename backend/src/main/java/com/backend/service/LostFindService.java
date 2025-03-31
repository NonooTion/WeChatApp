package com.backend.service;

import com.backend.entity.LostFind;
import com.backend.entity.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface LostFindService {
    // 发布失物招领信息
    Result<?> publish(LostFind lostFind);
    
    // 更新失物招领信息
    Result<?> update(LostFind lostFind);
    
    // 删除失物招领信息
    Result<?> delete(Integer lfId);
    
    // 获取失物招领信息详情
    Result<?> getDetail(Integer lfId);
    
    // 分页获取失物招领列表
    Result<Page<LostFind>> getList(Integer pageNum, Integer pageSize, String status, String keyword, String type);
    
    // 获取用户发布的失物招领列表
    Result<Page<LostFind>> getUserList(Integer userId, Integer pageNum, Integer pageSize, String type);
    
    // 上传失物招领图片
    Result<Map<String, Object>> uploadImage(MultipartFile file);
} 