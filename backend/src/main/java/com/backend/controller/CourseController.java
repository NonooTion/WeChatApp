package com.backend.controller;

import com.backend.entity.Code;
import com.backend.entity.Course;
import com.backend.entity.Result;
import com.backend.service.CourseService;
import com.backend.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    CourseService courseService;
    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/add")
    public Result<?> addCourse(
            @RequestBody Course course,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization))
        {
            return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        System.out.println(course);
        System.out.println(userId);
        return courseService.addCourse(userId, course.getCourseName(), course.getDay(), course.getTime(),course.getClassroom());
    }

    @PutMapping("/update")
    public Result<?> updateCourse(
            @RequestParam Integer courseId,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String classroom,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization))
        {
            return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
        }
        return courseService.updateCourse(courseId, courseName, classroom);
    }

    @DeleteMapping("/delete")
    public Result<?> deleteCourse(
            @RequestBody Course course,
            @RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization))
        {
            return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
        }
        return courseService.deleteCourse(course.getCourseId());
    }

    @GetMapping("/get-by-user-id")
    public Result<?> getTodosByUserId(@RequestHeader("Authorization") String authorization) {
        if(jwtUtil.isTokenExpired(authorization))
        {
            return Result.fail(Code.CODE_UNAUTHORIZED,"Token过期，请重新登录");
        }
        Integer userId = jwtUtil.getUserIdFromToken(authorization);
        return courseService.getCoursesByUserId(userId);
    }
}
