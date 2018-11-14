package com.xuecheng.manage.cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage.cms.config.RabbitMQConfig;
import com.xuecheng.manage.cms.dao.CmsPageRepository;
import com.xuecheng.manage.cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private RabbitTemplate rabbitTemplate;

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
        //更新dataUrl
        save.setDataUrl(cmsPage.getDataUrl());
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

    /**
     * 生成静态页面
     *
     * @param pageId 页面ID
     * @return 静态页面
     * @throws IOException 异常
     */
    public String generateStaticHtml(String pageId)  {
        if (StringUtils.isEmpty(pageId)) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        //1.获取数据模型
        Map data_model = getDataModelByPageId(pageId);
        //2.获取模版
        String static_html= null;
        try {
            String template_string= getTemplateByPageId(pageId);
            //3.生成静态页面
            static_html = mergeDataModelAndTemplate(data_model,template_string);
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionCast.cast(CommonCode.SERVER_ERROR);
        } catch (TemplateException e) {
            e.printStackTrace();
            ExceptionCast.cast(CommonCode.SERVER_ERROR);
        }
        return static_html;
    }

    //3.生成静态页面
    private String mergeDataModelAndTemplate(Map data_model, String template_string) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setDefaultEncoding("utf-8");
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",template_string);
        configuration.setTemplateLoader(stringTemplateLoader);
        Template template = configuration.getTemplate("template");
        //输出到硬盘
        Writer out = new FileWriter(new File("D:/Temp/xcStaticHtml/index_banner.html"));
        template.process(data_model,out);
        out.close();
        //生成字符串
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, data_model);
        if(StringUtils.isEmpty(html)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        return html;
    }

    //2.获取模版
    private String getTemplateByPageId(String pageId) throws IOException {
        CmsPage cmsPage = findById(pageId).getCmsPage();
        if(StringUtils.isEmpty(cmsPage.getTemplateId())) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        Optional<CmsTemplate> one = cmsTemplateRepository.findById(cmsPage.getTemplateId());
        if(!one.isPresent()) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        CmsTemplate cmsTemplate = one.get();
        String templateFileId = cmsTemplate.getTemplateFileId();
        if(StringUtils.isEmpty(templateFileId)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        String tempalte = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        if(StringUtils.isEmpty(tempalte)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        return tempalte;
    }

    //1.获取数据模型
    private Map getDataModelByPageId(String pageId) {
        CmsPageResult cmsPageResult = findById(pageId);
        CmsPage cmsPage = cmsPageResult.getCmsPage();
        if(StringUtils.isEmpty(cmsPage.getDataUrl())) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(cmsPage.getDataUrl(), Map.class);
        if(Objects.isNull(forEntity)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        Map body = forEntity.getBody();
        if(Objects.isNull(body)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        return body;
    }

    /**
     * 发布页面
     *
     * @param pageId 页面ID
     * @return 操作结果
     */
    public ResponseResult postPage(String pageId) {
        if (StringUtils.isEmpty(pageId)) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        //1.生成静态页面
        String staticHtml = generateStaticHtml(pageId);
        //2.将静态页面存入MongoDB中,并更新CmsPage
        savaStaticHtmlToGridFS(pageId,staticHtml);
        //3.发送消息到RabbitMQ
        sendToRabbitMQ(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //发送消息到RabbitMQ
    private void sendToRabbitMQ(String pageId) {
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if(!optionalCmsPage.isPresent()) ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        CmsPage cmsPage = optionalCmsPage.get();
        Map<String,String> msg = new HashMap<>();
        msg.put("pageId",pageId);
        String msgString = JSON.toJSONString(msg);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CMS_EXCHANGE_DIRECT,cmsPage.getSiteId(),msgString);
    }

    //将静态页面存入MongoDB中,并更新CmsPage
    private void savaStaticHtmlToGridFS(String pageId,String staticHtml) {
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if(!optionalCmsPage.isPresent()) ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        CmsPage cmsPage = optionalCmsPage.get();
        if(!StringUtils.isEmpty(cmsPage.getHtmlFileId())) gridFsTemplate.delete(Query.query(Criteria.where("_id").is(cmsPage.getHtmlFileId())));
        try {
            InputStream in = IOUtils.toInputStream(staticHtml, "utf-8");
            ObjectId objectId = gridFsTemplate.store(in, cmsPage.getPageName());
            cmsPage.setHtmlFileId(objectId.toString());
            cmsPageRepository.save(cmsPage);
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_SAVEHTMLERROR);
        }
    }
}
