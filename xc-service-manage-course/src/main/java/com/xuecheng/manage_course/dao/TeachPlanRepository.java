package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachPlanRepository extends JpaRepository<Teachplan,String> {
    //根据父结点和课程Id查询结点列表
    List<Teachplan> findAllByCourseidAndParentid(String courseId, String parentId);
}
