package com.xuecheng.manage.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage.cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Dao接口测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ManageCmsApplication.class)
public class DaoTest {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    /**
     * 查询所有页面信息
     */
    @Test
    public void testFindAll(){
        List<CmsPage> cmsPages = cmsPageRepository.findAll();
        for (CmsPage cmsPage : cmsPages) {
            System.out.println(cmsPage);
        }
    }
}
