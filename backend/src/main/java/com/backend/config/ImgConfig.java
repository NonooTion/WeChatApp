package com.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

import java.io.File;

@Configuration
public class ImgConfig implements WebMvcConfigurer {
    
    @Value("${file.save.path}")
    private String fileSavePath;
    
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 获取绝对路径
        String absolutePath = new File(fileSavePath).getAbsolutePath();
        System.out.println("图片访问路径: " + absolutePath); // 添加日志
        System.out.println("fileSavePath: " + fileSavePath);
        // 配置图片访问路径
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:///" + absolutePath.replace("\\", "/") + "/");
        
        // 配置失物招领图片访问路径
        registry.addResourceHandler("/image/lost-found/**")
                .addResourceLocations("file:///" + absolutePath.replace("\\", "/") + "/lost-found/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
