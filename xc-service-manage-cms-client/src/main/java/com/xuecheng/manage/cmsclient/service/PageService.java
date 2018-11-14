package com.xuecheng.manage.cmsclient.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage.cmsclient.dao.CmsPageRepository;
import com.xuecheng.manage.cmsclient.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 发面页面
     *
     * @param pageId
     */
    public void postPageToServer(String pageId){
        if(StringUtils.isEmpty(pageId)) ExceptionCast.cast(CommonCode.NULL_PARAM);
        //1.获得静态页面
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if(!optionalCmsPage.isPresent()) ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        String htmlFileId = optionalCmsPage.get().getHtmlFileId();
        String staticHtml = getStaticHtml(htmlFileId);
        //2.获得物理路径
        String siteId = optionalCmsPage.get().getSiteId();
        String serverPath = getServerPath(siteId,optionalCmsPage.get());
        //3.发布到服务器
        try {
            Writer writer = new FileWriter(serverPath);
            writer.write(staticHtml);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionCast.cast(CommonCode.FAIL);
        }
    }

    //2.获得物理路径
    private String getServerPath(String siteId,CmsPage cmsPage) {
        if(StringUtils.isEmpty(siteId)) ExceptionCast.cast(CmsCode.CMS_POSTHTML_SITEDATAISNULL);
        Optional<CmsSite> optionalCmsSite = cmsSiteRepository.findById(siteId);
        if(!optionalCmsSite.isPresent()) ExceptionCast.cast(CmsCode.CMS_POSTHTML_SITEDATAISNULL);
        String siteWebPath = optionalCmsSite.get().getSiteWebPath();
        if(StringUtils.isEmpty(siteWebPath)) ExceptionCast.cast(CmsCode.CMS_POSTHTML_SITEWEBPATHISNULL);
        String pagePhysicalPath = cmsPage.getPagePhysicalPath();
        if(StringUtils.isEmpty(pagePhysicalPath)) ExceptionCast.cast(CmsCode.CMS_POSTHTML_PAGEPHYSICALPATHISNULL);
        String serverPath=pagePhysicalPath+siteWebPath+cmsPage.getPageName();
        return serverPath;
    }

    //1.获得静态页面
    private String getStaticHtml(String htmlFileId) {
        if(StringUtils.isEmpty(htmlFileId)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        String staticHtml = null;
        try {
            staticHtml = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_SAVEHTMLERROR);
        }
        return staticHtml;
    }
}
