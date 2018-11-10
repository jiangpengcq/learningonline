package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;


    /**
     * 页面列表分页查询
     *
     * @param page             当前页码
     * @param size             页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页面列表
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) queryPageRequest = new QueryPageRequest();
        //分页对象-pageable
        if (page <= 0) page = 1;
        page = page - 1;//为了适应mongodb的接口将页码减1
        if (size <= 0) size = 20;
        Pageable pageable = new PageRequest(page, size);
        //条件对象-exmaple
        CmsPage cmsPage = new CmsPage();
        if (!StringUtils.isEmpty(queryPageRequest.getPageAliase()))
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        if (!StringUtils.isEmpty(queryPageRequest.getSiteId())) cmsPage.setSiteId(queryPageRequest.getSiteId());
        if (!StringUtils.isEmpty(queryPageRequest.getTemplateId()))
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> exmaple = Example.of(cmsPage, matcher);
        //查询结果
        Page<CmsPage> cmsPages = cmsPageRepository.findAll(exmaple, pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
        cmsPageQueryResult.setList(cmsPages.getContent());
        cmsPageQueryResult.setTotal(cmsPages.getTotalElements());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
    }

    /**
     * 添加页面
     *
     * @param cmsPage 页面参数
     * @return 页面内容
     */
    public CmsPageResult save(CmsPage cmsPage) {
        //if (cmsPage == null) return new CmsPageResult(CommonCode.FAIL, null);
        if (cmsPage == null) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        CmsPage one = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //if (one != null) return new CmsPageResult(CommonCode.FAIL, null);
        if (one != null) ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        cmsPage.setPageId(null);
        CmsPage save = cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, save);
    }

    /**
     * 按ID查找页面
     *
     * @param pageId 页面ID
     * @return 页面内容
     */
    public CmsPageResult findById(String pageId) {
        //if(StringUtils.isEmpty(pageId)) return new CmsPageResult(CommonCode.FAIL,null);
        if (StringUtils.isEmpty(pageId)) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        Optional<CmsPage> one = cmsPageRepository.findById(pageId);
        if (!one.isPresent()) ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        return new CmsPageResult(CommonCode.SUCCESS, one.get());
    }

    /**
     * 更新页面
     *
     * @param cmsPage 页面参数
     * @return 页面内容
     */
    public CmsPageResult update(CmsPage cmsPage) {
        //if(Objects.isNull(cmsPage)) return new CmsPageResult(CommonCode.FAIL,cmsPage);
        if (Objects.isNull(cmsPage)) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        Optional<CmsPage> one = cmsPageRepository.findById(cmsPage.getPageId());
        if (!one.isPresent()) ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        CmsPage save = one.get();
        //更新模板id
        save.setTemplateId(cmsPage.getTemplateId());
        //更新所属站点
        save.setSiteId(cmsPage.getSiteId());
        //更新页面别名
        save.setPageAliase(cmsPage.getPageAliase());
        //更新页面名称
        save.setPageName(cmsPage.getPageName());
        //更新访问路径
        save.setPageWebPath(cmsPage.getPageWebPath());
        //更新物理路径
        save.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
        //执行更新
        cmsPageRepository.save(save);
        return new CmsPageResult(CommonCode.SUCCESS, save);
    }

    /**
     * 按ID删除页面
     *
     * @param pageId 页面ID
     * @return 操作结果
     */
    public ResponseResult delete(String pageId) {
        //if(StringUtils.isEmpty(pageId)) return new ResponseResult(CommonCode.FAIL);
        if (StringUtils.isEmpty(pageId)) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        Optional<CmsPage> one = cmsPageRepository.findById(pageId);
        if (!one.isPresent()) ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        cmsPageRepository.deleteById(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

}
