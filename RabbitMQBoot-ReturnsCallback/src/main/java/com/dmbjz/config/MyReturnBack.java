package com.dmbjz.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/** 自定义ReturnCallBack 实现类 */
@Component
public class MyReturnBack implements RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ConnectionFactory connectionFactory;

    @PostConstruct
    public void init()
    {
        rabbitTemplate.setReturnsCallback(this);
        rabbitTemplate.setConnectionFactory(connectionFactory);
    }


    @Override
    public void returnedMessage(ReturnedMessage returned) {

        System.out.println("消息相应码 : "+returned.getReplyCode());
        System.out.println("消息主体 message : "+returned.getMessage());
        System.out.println("描述："+returned.getReplyText());
        System.out.println("消息使用的交换器 exchange : "+ returned.getMessage());
        System.out.println("消息使用的路由键 routiAng : "+ returned.getRoutingKey());

    }



}
