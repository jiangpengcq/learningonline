package com.xuecheng.manage.cms.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    @Value("${xuecheng.mq.exchange}")
    private String EXCHANGE_NAME_POSTPAGE;
    //交换机Bean的名称
    public  static final String CMS_EXCHANGE_DIRECT="ex_routing_cms_postpage";

    //定义交换机-路由工作模式
    @Bean(CMS_EXCHANGE_DIRECT)
    public Exchange CMS_EXCHANGE(){
        return ExchangeBuilder.directExchange(EXCHANGE_NAME_POSTPAGE).durable(true).build();
    }
}
