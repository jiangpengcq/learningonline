package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.springframework.stereotype.Component;

@Component
public interface TeachPlanMapper {

    //查询课程计划列表
    TeachplanNode findTeachPlanList(String courseId);

    //添加

}
