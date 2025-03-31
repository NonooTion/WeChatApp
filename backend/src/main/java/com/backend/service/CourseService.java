package com.backend.service;

import com.backend.entity.Code;
import com.backend.entity.Course;
import com.backend.entity.Result;
import com.backend.entity.Todo;
import com.backend.mapper.CourseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class CourseService {
    @Autowired
    CourseMapper courseMapper;

    public Result<?> addCourse(Integer userId,
                               String courseName,
                               String day,
                               String time,
                               String classroom)
    {
        try{
            Course course =new Course();
            course.setUserId(userId);
            course.setCourseName(courseName);
            course.setDay(day);
            course.setTime(time);
            course.setClassroom(classroom);

            int rows = courseMapper.insert(course);
            if(rows>0){
                return Result.success("课程添加成功");
            }else {
                return Result.fail(Code.CODE_SERVER_ERROR, "课程添加失败");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "课程添加失败");
        }
    }

    public Result<?> updateCourse(Integer courseId,
                                String courseName,
                                String classroom
                                )
    {
        try {
            Course course = courseMapper.selectById(courseId);
            if (course == null) {
                return Result.fail(Code.CODE_NOT_FOUND, "课程不存在");
            }

            if (courseName != null) {
                course.setCourseName(courseName);
            }
            if (classroom != null) {
                course.setClassroom(classroom);
            }


            int rows = courseMapper.updateById(course);
            if (rows > 0) {
                return Result.success("课程修改成功");
            } else {
                return Result.fail(Code.CODE_SERVER_ERROR, "课程修改失败");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "课程修改异常: " + e.getMessage());
        }
    }

    public Result<?> deleteCourse(Integer courseId) {
        try {
            System.out.println(courseId);
            Course course = courseMapper.selectById(courseId);
            if (course == null) {
                return Result.fail(Code.CODE_NOT_FOUND, "课程不存在");
            }

            int rows = courseMapper.deleteById(courseId);
            if (rows > 0) {
                return Result.success("课程删除成功");
            } else {
                return Result.fail(Code.CODE_SERVER_ERROR, "课程删除失败");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "课程删除异常: " + e.getMessage());
        }
    }

    public Result<?> getCoursesByUserId(Integer userId) {
        try {
            List<Course> courses = courseMapper.selectList(new QueryWrapper<Course>().eq("user_id",userId));
            if (courses != null) {
                return Result.success(courses);
            } else {
                return Result.fail(Code.CODE_NOT_FOUND, "未找到该用户的课程");
            }
        } catch (Exception e) {
            return Result.fail(Code.CODE_SERVER_ERROR, "查询课程异常: " + e.getMessage());
        }
    }
}
