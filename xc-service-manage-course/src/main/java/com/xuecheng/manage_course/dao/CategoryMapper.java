package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.springframework.stereotype.Component;

@Component
public interface CategoryMapper {
    //查询分类
    CategoryNode selectList();
}
