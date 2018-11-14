package com.xuecheng.manage.cmsclient.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    @Value("${xuecheng.mq.exchange}")
    public String EXCHANGE_NAME_POSTPAGE;
    @Value("${xuecheng.mq.queue}")
    public String QUEUE_NAME_POSTPAGE;
    @Value("${xuecheng.mq.routingKey}")
    public String ROUTING_KEY;

    //定义交换机-路由工作模式
    @Bean
    public Exchange CMS_EXCHANGE(){
        return ExchangeBuilder.directExchange(EXCHANGE_NAME_POSTPAGE).durable(true).build();
    }

    //定义队列
    @Bean
    public Queue CMS_QUEUE(){
        return new Queue(QUEUE_NAME_POSTPAGE);
    }

    //绑定队列与交换机
    @Bean
    public Binding binding(@Qualifier("CMS_EXCHANGE") Exchange exchange,@Qualifier("CMS_QUEUE") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY).noargs();
    }

}
