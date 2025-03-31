package com.backend.service;

import com.backend.entity.Code;
import com.backend.entity.Result;
import com.backend.entity.Todo;
import com.backend.entity.User;
import com.backend.mapper.UserMapper;
import com.backend.util.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Value("${file.save.path}")
    String fileSavePath;
    @Autowired
    UserMapper userMapper;
    @Autowired
    JWTUtil jwtUtil;
    public Result<Map<String,Object>> login(String usernameOrPhone, String password)
    {
        User user = validateUser(usernameOrPhone, password);
        if (user == null) {
            // 用户名或密码错误
            return Result.fail(Code.CODE_UNAUTHORIZED, "用户名或密码错误");
        }

        // 登录成功，生成token
        String token = jwtUtil.generateToken(user);

        // 准备返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }
    public User validateUser(String usernameOrPhone, String password) {

        // 根据用户名或手机号查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("username", usernameOrPhone)
                .or()
                .eq("phone", usernameOrPhone));

        if (user != null) {
            // 对输入的密码进行哈希处理
            byte[] passwordHash = DigestUtils.md5Digest(password.getBytes());
            String hexString = HexUtils.toHexString(passwordHash);
            // 获取数据库中存储的密码哈希值（字符串形式）
            String storedPasswordHash = user.getPassword();
            // 比较哈希值
            if (hexString.equals(storedPasswordHash)) {
                return user;
            }
        }

        return null;
    }
    public Result <Map<String,Object>> register(String username,String phone,String password)
    {
        boolean userExists = isUserExists(username, phone);
        if(userExists)
        {
            return Result.fail(Code.CODE_PARAM_ERROR,"用户名或手机号已注册");
        }
        String hashedPassword= DigestUtils.md5DigestAsHex(password.getBytes());
        User user=new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(hashedPassword);
        user.setName("用户"+username);
        user.setImgUrl("/image/default.png");
        boolean save_success = userMapper.insert(user)>0;
        if(save_success)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getUserId());
            data.put("message", "注册成功");

            return Result.success(data);
        }
        return Result.fail(Code.CODE_SERVER_ERROR, "注册失败，请稍后再试");
    }
    public Result<Map<String, Object>> forgetPassword(String username,String newPassword) {

        // 验证用户名是否存在
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            return Result.fail(Code.CODE_UNAUTHORIZED, "用户名不存在");
        }

        // 对新密码进行哈希处理
        byte[] passwordHash = DigestUtils.md5Digest(newPassword.getBytes());
        String passwordHashHex = HexUtils.toHexString(passwordHash);

        // 更新用户密码
        user.setPassword(passwordHashHex);
        boolean updateResult = userMapper.updateById(user)>0;

        if (updateResult) {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "密码重置成功");

            return Result.success(data);
        } else {
            return Result.fail(Code.CODE_SERVER_ERROR, "密码重置失败，请稍后再试");
        }
    }

    private boolean isUserExists(String username, String phone) {
        // 查询用户名是否已存在
        User userByUsername = userMapper.selectOne(new QueryWrapper<User>().eq("username", username).eq("username",phone));
        if (userByUsername != null) {
            return true;
        }

        // 查询手机号是否已存在
        User userByPhone = userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone).or().eq("phone",username));
        if (userByPhone != null) {
            return true;
        }
        return false;
    }

    public Result<?> getUserByUserId(Integer userId) {
        try {
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("user_id", userId));
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.fail(Code.CODE_NOT_FOUND, "未找到该用户的日程");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "查询日程异常: " + e.getMessage());
        }
    }

    public Result<?> updateTodo(Integer userId, String name, String email, String phone) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.fail(Code.CODE_NOT_FOUND, "用户不存在");
            }

            if (name != null) {
                user.setName(name);
            }
            if (email != null) {
                user.setEmail(email);
            }
            if (phone != null) {
                user.setPhone(phone);
            }

            int rows = userMapper.updateById(user);
            if (rows > 0) {
                return Result.success("用户信息修改成功");
            } else {
                return Result.fail(Code.CODE_SERVER_ERROR, "用户信息修改失败");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "用户信息修改异常: " + e.getMessage());
        }
    }
    public Result<Map<String, Object>> uploadAvatar(Integer userId, MultipartFile file) {
        System.out.println("图片上传");

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

            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.fail(Code.CODE_NOT_FOUND, "用户不存在");
            }

            // 获取用户名
            String username = user.getUsername();

            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

            // 生成新的文件名（用户名 + 时间戳 + 扩展名）
            String newFilename = username + "_" + System.currentTimeMillis() + "." + fileExtension;

            // 获取当前工作目录
            String currentDir = System.getProperty("user.dir");
            // 拼接 image 目录路径（与 src 同级）
            String imageDirPath = currentDir + File.separator + "image";
            File saveDir = new File(imageDirPath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // 删除旧文件
            deleteOldFiles(username, saveDir);

            // 保存文件
            File destFile = new File(saveDir, newFilename);
            file.transferTo(destFile);

            // 更新用户头像 URL
            String avatarUrl = "/image/" + newFilename;
            user.setImgUrl(avatarUrl);
            userMapper.updateById(user);

            Map<String, Object> data = new HashMap<>();
            data.put("url", avatarUrl);
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(Code.CODE_SERVER_ERROR, "上传失败: " + e.getMessage());
        }
    }

    private void deleteOldFiles(String username, File saveDir) {
        // 获取目录中的所有文件
        File[] files = saveDir.listFiles();
        if (files != null) {
            // 遍历文件，删除相同用户名前缀的文件
            for (File file : files) {
                if (file.getName().startsWith(username + "_")) {
                    if (file.delete()) {
                        System.out.println("旧文件已删除: " + file.getName());
                    } else {
                        System.out.println("无法删除旧文件: " + file.getName());
                    }
                }
            }
        }
    }
}


