package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator.
 */

@Component
public interface CourseMapper {
    //查找一个
   CourseBase findCourseBaseById(String id);
    //查询所有课程
   Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);

}
