package com.xuecheng.manage.cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage.cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/cms/page")
public class CmsPageController extends BaseController implements CmsPageControllerApi {

    @Autowired
    private PageService pageService;

    @Override
    @GetMapping("/list/{page}/{size}")
    @ResponseBody
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {
        return pageService.findList(page, size, queryPageRequest);
    }

    @Override
    @PostMapping("/add")
    @ResponseBody
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.save(cmsPage);
    }

    @Override
    @GetMapping("/find/{pageId}")
    @ResponseBody
    public CmsPageResult findById(@PathVariable("pageId") String pageId) {
        return pageService.findById(pageId);
    }

    @Override
    @PutMapping("/update")
    @ResponseBody
    public CmsPageResult update(@RequestBody CmsPage cmsPage) {
        return pageService.update(cmsPage);
    }

    @Override
    @DeleteMapping("/delete/{pageId}")
    @ResponseBody
    public ResponseResult delete(@PathVariable("pageId") String pageId) {
        return pageService.delete(pageId);
    }

    @Override
    @GetMapping("/preview/{pageId}")
    public void preview(@PathVariable("pageId") String pageId) {
        String staticHtml = pageService.generateStaticHtml(pageId);
        try {
            ServletOutputStream out = response.getOutputStream();
            out.write(staticHtml.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionCast.cast(CmsCode.CMS_COURSE_PERVIEWISNULL);
        }
    }

    @Override
    @PutMapping("/postPage/{pageId}")
    @ResponseBody
    public ResponseResult postPage(@PathVariable("pageId") String pageId) {
        return pageService.postPage(pageId);
    }
}
