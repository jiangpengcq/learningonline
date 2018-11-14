package com.xuecheng.manage.cmsclient.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage.cmsclient.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * RabbitMQ消费者
 */
@Component
public class ConsumerPostPage {

    @Autowired
    private PageService pageService;

    /**
     * 发布页面
     * @param msg
     */
    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg){
//        String msg =null;
//        try {
//           msg = new String(arrs, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        if(StringUtils.isEmpty(msg)) ExceptionCast.cast(CommonCode.NULL_PARAM);
        Map map = JSON.parseObject(msg, Map.class);
        pageService.postPageToServer(map.get("pageId").toString());
    }
}
