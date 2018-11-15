package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    @Autowired
    private CourseMarketRepository courseMarketRepository;
    @Autowired
    private TeachPlanMapper teachPlanMapper;
    @Autowired
    private TeachPlanRepository teachPlanRepository;

    /**
     * 查询所有课程
     *
     * @param page 开始页码
     * @param size 每页条数
     * @param courseListRequest 请求参数
     * @return 课程集合
     */
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest){
        if (ObjectUtils.isEmpty(courseListRequest)) courseListRequest = new CourseListRequest();
        if (page <= 0) page=0;
        if (size <= 0) size=20;
        PageHelper.startPage(page,size);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setList(courseListPage.getResult());
        queryResult.setTotal(courseListPage.getTotal());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    /**
     * 添加课程
     *
     * @param courseBase 基础课程信息
     * @return 操作结果
     */
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        //课程状态默认为未发布
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS,courseBase.getId());
    }

    /**
     * 查询课程
     *
     * @param courseId 课程ID
     * @return 课程信息
     */
    public CourseBase getCoursebaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    /**
     * 编辑课程
     *
     * @param id 课程ID
     * @param courseBase 基础课程
     * @return 操作结果
     */
    @Transactional
    public ResponseResult updateCoursebase(String id, CourseBase courseBase) {
        CourseBase one = this.getCoursebaseById(id);
        if(one == null){
            //抛出异常..
            ExceptionCast.cast(CommonCode.NULL_PARAM);
        }
        //修改课程信息
        one.setName(courseBase.getName());
        one.setMt(courseBase.getMt());
        one.setSt(courseBase.getSt());
        one.setGrade(courseBase.getGrade());
        one.setStudymodel(courseBase.getStudymodel());
        one.setUsers(courseBase.getUsers());
        one.setDescription(courseBase.getDescription());
        courseBaseRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查询课程营销信息
     *
     * @param courseId 营销ID
     * @return 课程营销信息
     */
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    /**
     * 更新课程营销信息
     *
     * @param id 营销ID
     * @param courseMarket 营销信息
     * @return 课程营销信息
     */
    @Transactional
    public CourseMarket updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket one = this.getCourseMarketById(id);
        if(one!=null){
            one.setCharge(courseMarket.getCharge());
            one.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
            one.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
            one.setPrice(courseMarket.getPrice());
            one.setQq(courseMarket.getQq());
            one.setValid(courseMarket.getValid());
            courseMarketRepository.save(one);
        }else{
            //添加课程营销信息
            one = new CourseMarket();
            BeanUtils.copyProperties(courseMarket, one);
            //设置课程id
            one.setId(id);
            courseMarketRepository.save(one);
        }
        return one;
    }

    /**
     * 查询课程计划
     *
     * @param courseId 课程ID
     * @return 课程计划
     */
    public TeachplanNode findTeachPlanList(String courseId){
        if (StringUtils.isEmpty(courseId)) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        TeachplanNode teachPlanList = teachPlanMapper.findTeachPlanList(courseId);
        if (ObjectUtils.isEmpty(teachPlanList)) ExceptionCast.cast(CourseCode.COURSE_PLAN_DATAISNULL);
        return teachPlanList;
    }

    /**
     * 添加课程计划
     *
     * @param teachplan 课程计划
     * @return 操作结果
     */
    @Transactional
    public ResponseResult addTeachPlan(Teachplan teachplan) {
        if (ObjectUtils.isEmpty(teachplan) || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        CourseBase courseBase = getCoursebaseById(teachplan.getCourseid());
        if (ObjectUtils.isEmpty(courseBase)) ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEBASEISNULL);
        //判断是否有一级结点并获得父结点
        Teachplan teachplanParent = getTeachPlanParent(courseBase,teachplan.getParentid());
        int grade = Integer.parseInt(teachplanParent.getGrade())+1;
        teachplan.setGrade(grade+"");
        teachplan.setParentid(teachplanParent.getId());
        teachPlanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获得父结点（新课程就创建一级结点）
    private Teachplan getTeachPlanParent(CourseBase courseBase, String parentId) {
        Teachplan teachplanParent =null;
        //查询一级结点
        List<Teachplan> teachplans = teachPlanRepository.findAllByCourseidAndParentid(courseBase.getId(), "0");
        if (ObjectUtils.isEmpty(teachplans)) {
            //1.1 没有父课程计划会认为是新课程没有一级课程计划，需要创建一级课程计划并保存
            teachplanParent = new Teachplan();
            //保存课程Id
            teachplanParent.setCourseid( courseBase.getId() );
            //一级课程计划默认一级的为1
            teachplanParent.setGrade( "1" );
            //一级课程发布状态 0为未发布 1为发布
            teachplanParent.setStatus( "0" );
            //一级课程计划的父Id为0
            teachplanParent.setParentid( "0" );
            //一级课程计划的名称为课程名称
            teachplanParent.setPname( courseBase.getName() );
            //一级课程计划的级别为1
            teachplanParent.setOrderby( 1 );
            //一级课程的描述为课程描述
            teachplanParent.setDescription( courseBase.getDescription() );
            teachplanParent = teachPlanRepository.save( teachplanParent );
        } else {
            //1.2 如果有父课程计划则查询课程计划
            if(StringUtils.isEmpty( parentId )) {
                teachplanParent = teachplans.get( 0 );
            } else {
                Optional<Teachplan> tParentoptional = teachPlanRepository.findById( parentId );
                if(!tParentoptional.isPresent())
                    ExceptionCast.cast( CommonCode.INVALID_PARAM );
                teachplanParent = tParentoptional.get();
            }
        }
        return teachplanParent;
    }
}
