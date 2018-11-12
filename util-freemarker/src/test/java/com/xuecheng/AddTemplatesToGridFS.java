package com.xuecheng;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * FreeMarker工具类
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AddTemplatesToGridFS {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    /**
     * 将Banner轮播图模版存入MongoBD GridFS中
     *
     * @throws FileNotFoundException
     */
    @Test
    public void addBannersToGridFS() throws FileNotFoundException {
        FileInputStream in = new FileInputStream("D:\\IDEA\\XCEdu\\SourceCode\\util-freemarker\\src\\test\\resources\\templates\\index_banner.ftl");
        ObjectId objectId = gridFsTemplate.store(in, "轮播图测试文件", "");
        String id = objectId.toString();
        System.out.println(id);
    }

    @Test
    public void testClassPath(){
        String path = this.getClass().getClassLoader().getResource("templates/").getPath();
        System.out.println(path);
        String path1 = this.getClass().getResource("").getPath();
        System.out.println(path1);
    }
}
