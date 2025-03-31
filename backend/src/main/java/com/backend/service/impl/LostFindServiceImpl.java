package com.backend.service.impl;

import com.backend.entity.LostFind;
import com.backend.entity.Result;
import com.backend.entity.User;
import com.backend.entity.Code;
import com.backend.Response.LostFindResponse;
import com.backend.mapper.LostFindMapper;
import com.backend.mapper.UserMapper;
import com.backend.service.LostFindService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class LostFindServiceImpl implements LostFindService {
    
    @Autowired
    private LostFindMapper lostFindMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Result<?> publish(LostFind lostFind) {
        lostFind.setCreateTime(LocalDateTime.now());
        lostFind.setStatus("待处理");
        if(lostFind.getItemImgUrl()==null||lostFind.getItemImgUrl().trim().isEmpty())
        {
            lostFind.setItemImgUrl("/image/lost-found/default.png");
        }
        int insert = lostFindMapper.insert(lostFind);
        if (insert > 0) {
            return Result.success();
        }
        return Result.fail(500, "发布失败");
    }
    
    @Override
    public Result<?> update(LostFind lostFind) {
        System.out.println(lostFind);
        int update = lostFindMapper.updateById(lostFind);
        if (update > 0) {
            return Result.success();
        }
        return Result.fail(500, "更新失败");
    }
    
    @Override
    public Result<?> delete(Integer lfId) {
        int delete = lostFindMapper.deleteById(lfId);
        if (delete > 0) {
            return Result.success();
        }
        return Result.fail(500, "删除失败");
    }
    
    @Override
    public Result<?> getDetail(Integer lfId) {
        LostFind lostFind = lostFindMapper.selectById(lfId);
        if (lostFind != null) {
            User user = userMapper.selectById(lostFind.getUserId());
            LostFindResponse response = new LostFindResponse();
            response.setLfId(lostFind.getLfId());
            response.setUserId(lostFind.getUserId());
            response.setTitle(lostFind.getTitle());
            response.setContent(lostFind.getContent());
            response.setLocation(lostFind.getLocation());
            response.setStatus(lostFind.getStatus());
            response.setItemImgUrl(lostFind.getItemImgUrl());
            response.setCreateTime(lostFind.getCreateTime());
            response.setType(lostFind.getType());
            if (user != null) {
                response.setName(user.getName());
                response.setPhone(user.getPhone());
                response.setEmail(user.getEmail());
            }
            return Result.success(response);
        }
        return Result.fail(404, "未找到该信息");
    }
    
    @Override
    public Result<Page<LostFind>> getList(Integer pageNum, Integer pageSize, String status, String keyword, String type) {
        System.out.println(type);
        Page<LostFind> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LostFind> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(LostFind::getStatus, status);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(LostFind::getType, type);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(LostFind::getTitle, keyword)
                  .or()
                  .like(LostFind::getContent, keyword)
                  .or()
                  .like(LostFind::getLocation, keyword);
        }
        wrapper.orderByDesc(LostFind::getCreateTime);
        Page<LostFind> result = lostFindMapper.selectPage(page, wrapper);
        return Result.success(result);
    }
    
    @Override
    public Result<Page<LostFind>> getUserList(Integer userId, Integer pageNum, Integer pageSize, String type) {
        Page<LostFind> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LostFind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LostFind::getUserId, userId);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(LostFind::getType, type);
        }
        wrapper.orderByDesc(LostFind::getCreateTime);
        return Result.success(lostFindMapper.selectPage(page, wrapper));
    }
    
    @Override
    public Result<Map<String, Object>> uploadImage(MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file == null || file.isEmpty()) {
                return Result.fail(Code.CODE_PARAM_ERROR, "文件不能为空");
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                return Result.fail(Code.CODE_PARAM_ERROR, "只支持图片文件");
            }

            // 检查文件大小
            long maxSize = 5 * 1024 * 1024 *5; // 5MB
            if (file.getSize() > maxSize) {
                return Result.fail(Code.CODE_PARAM_ERROR, "文件大小不能超过5MB");
            }

            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

            // 生成新的文件名（时间戳 + 随机数 + 扩展名）
            String newFilename = System.currentTimeMillis() + "_" + (int)(Math.random() * 1000) + "." + fileExtension;

            // 获取当前工作目录
            String currentDir = System.getProperty("user.dir");
            // 拼接 image/lost-found 目录路径
            String imageDirPath = currentDir + File.separator + "image" + File.separator + "lost-found";
            File saveDir = new File(imageDirPath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // 保存文件
            File destFile = new File(saveDir, newFilename);
            file.transferTo(destFile);

            // 返回文件URL
            Map<String, Object> data = new HashMap<>();
            data.put("url", "/image/lost-found/" + newFilename);
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(Code.CODE_SERVER_ERROR, "上传失败: " + e.getMessage());
        }
    }
} 