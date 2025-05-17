# 校园生活助手

一个基于微信小程序和Spring Boot的校园生活管理平台，帮助学生高效管理待办事项、课程表、话题交流和失物招领等校园生活场景。

## 功能特性

- **用户系统**：注册、登录、找回密码、个人信息管理、头像上传。
- **待办事项**：添加、编辑、删除、查询个人待办事项，支持优先级和提醒。
- **课程管理**：添加、编辑、删除、查询个人课程表。
- **话题交流**：发布、浏览、评论、点赞、删除话题，支持分页和关键词搜索。
- **失物招领**：发布、浏览、编辑、删除失物招领信息，支持图片上传和分页查询。

## 技术栈

- **前端**：微信小程序（使用[@vant/weapp](https://github.com/youzan/vant-weapp)组件库）
- **后端**：Spring Boot 3.2.3、MyBatis-Plus 3.5.5、JWT、MySQL
- **开发语言**：Java 17、JavaScript

## 目录结构

```
├── backend/                # 后端Spring Boot服务
│   ├── src/main/java/com/backend/
│   │   ├── controller/    # 各类业务接口
│   │   ├── service/       # 业务逻辑
│   │   ├── entity/        # 实体类
│   │   └── ...
│   └── ...
├── frontend/               # 微信小程序前端
│   ├── pages/             # 页面目录
│   ├── utils/             # 工具函数
│   ├── images/            # 图片资源
│   ├── app.js/json/wxss   # 小程序主文件
│   └── ...
```

## 快速开始

### 后端部署
1. 安装JDK 17、Maven、MySQL。
2. 配置`backend/src/main/resources/application.yml`数据库连接。
3. 在`backend`目录下执行：
   ```bash
   mvn clean package
   java -jar target/backend-0.0.1-SNAPSHOT.jar
   ```

### 前端部署
1. 安装[微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)。
2. 使用微信开发者工具导入`frontend`目录，配置AppID（如需）。
3. 安装依赖（如需）：
   ```bash
   cd frontend
   npm install
   ```
4. 运行并预览小程序。

## 接口文档
后端API为RESTful风格，主要接口包括：
- `/user` 用户相关
- `/todo` 待办事项
- `/course` 课程管理
- `/topic` 话题交流
- `/lost-find` 失物招领

详细接口说明可参考源码注释或后续补充的API文档。

## 贡献方式
欢迎提交Issue和PR！
1. Fork本仓库
2. 新建分支进行开发
3. 提交PR并描述你的更改

## License
本项目采用MIT License。

---
如有问题欢迎联系或提Issue。 