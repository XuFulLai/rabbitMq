package com.dmbjz.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/* 自定义实现不可路由消息兜底方法  */
@Slf4j
@Component
public class MyBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {


    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ConnectionFactory connectionFactory;

    /*将实现类注入
    *   注入顺序：Constructor(构造方法) -> @Autowired(依赖注入) -> @PostConstruct(注释的方法)
    * */
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
        rabbitTemplate.setConnectionFactory(connectionFactory);
    }


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        String id = correlationData.getId()!=null ? correlationData.getId() : "";

        if (ack){
            log.info("{}交换机接收到了消息,消息ID为:{}", correlationData.getReturned().getExchange(),id);
        }else{
            log.warn("{}交换机接收ID为{}的消息失败,原因:{}", correlationData.getReturned().getExchange(),id, cause);
        }

    }


    @Override
    public void returnedMessage(ReturnedMessage returned) {

        log.warn("不可路由消息相应码 : "+returned.getReplyCode());
        log.warn("不可路由消息主体 message : "+returned.getMessage());
        log.warn("不可路由消息描述："+returned.getReplyText());
        log.warn("不可路由消息使用的交换器 exchange : "+ returned.getMessage());
        log.warn("不可路由消息使用的路由键 routiAng : "+ returned.getRoutingKey());

    }

}
