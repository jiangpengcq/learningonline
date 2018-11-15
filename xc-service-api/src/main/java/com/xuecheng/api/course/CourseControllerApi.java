package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value="课程管理接口",description = "课程管理接口，提供课程的管理、查询接口")
public interface CourseControllerApi {
    /**
     * 根据条件分页查询课程信息集合
     *
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    @ApiOperation("查询我的课程列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true,  paramType = "path", dataType = "int")
    })
    QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

    @ApiOperation("添加课程基础信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseBase", value = "课程基础信息", required = true,  paramType = "body", dataType = "json")
    })
    AddCourseResult addCourseBase(CourseBase courseBase);

    @ApiOperation("获取课程基础信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程ID", required = true,  paramType = "path", dataType = "string")
    })
    CourseBase getCourseBaseById(String courseId) throws RuntimeException;

    @ApiOperation("更新课程基础信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程ID", required = true,  paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "courseBase", value = "课程基础信息", required = true,  paramType = "body", dataType = "json")
    })
    ResponseResult updateCourseBase(String id, CourseBase courseBase);

    @ApiOperation("获取课程营销信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程ID", required = true,  paramType = "path", dataType = "string")
    })
    CourseMarket getCourseMarketById(String courseId);

    @ApiOperation("更新课程营销信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程ID", required = true,  paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "courseMarket", value = "课程营销信息", required = true,  paramType = "body", dataType = "json")
    })
    ResponseResult updateCourseMarket(String id,CourseMarket courseMarket);

    @ApiOperation("查询课程计划信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId",value = "课程ID",required = true,paramType = "path",dataType = "string")
    })
    TeachplanNode findTeachPlanList(String courseId);

    @ApiOperation("添加课程计划信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teachplan",value = "课程计划",required = true,paramType = "body",dataType = "json")
    })
    ResponseResult addTeachPlan(Teachplan teachplan);
}
