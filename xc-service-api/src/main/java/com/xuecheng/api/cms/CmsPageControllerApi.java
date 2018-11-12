package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    /**
     * 根据条件分页查询CmsPage页面信息集合
     **/
    @ApiOperation("分页条件查询页面")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
        @ApiImplicitParam(name = "size", value = "每页记录数", required = true,  paramType = "path", dataType = "int")
    })
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    /**
     * 添加页面
     * @param cmsPage
     * @return
     */
    @ApiOperation("添加页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cmsPage", value = "添加页面参数",required = true,paramType = "body",dataType = "json")
    })
    CmsPageResult add(CmsPage cmsPage);

    /**
     * 按ID查询页面
     *
     * @param pageId
     * @return
     */
    @ApiOperation("按ID查询页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageId",value = "页面ID",required = true,paramType = "path",dataType = "string")
    })
    CmsPageResult findById(String pageId);

    /**
     * 编辑页面
     *
     * @param cmsPage
     * @return
     */
    @ApiOperation("编辑页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cmsPage", value = "修改页面参数",required = true,paramType = "body",dataType = "json")
    })
    CmsPageResult update(CmsPage cmsPage);

    /**
     * 按ID删除页面
     *
     * @param pageId
     * @return
     */
    @ApiOperation("按ID删除页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageId",value = "页面ID",required = true,paramType = "path",dataType = "string")
    })
    ResponseResult delete(String pageId);

    /**
     * 页面预览
     *
     * @param pageId
     */
    @ApiOperation("页面预览")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageId",value = "页面ID",required = true,paramType = "path",dataType = "string")
    })
    void preview(String pageId);
}
