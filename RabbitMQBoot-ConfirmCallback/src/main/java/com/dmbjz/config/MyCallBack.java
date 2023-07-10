package com.dmbjz.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/* 实现 RabbitTemplate的 ConfirmCallback 方法
*      ConfirmCallback（交换机回调方法）执行条件：
*           1、发送消息后 交换机接收到了消息
*           2、发送消息后 交换机接收消息失败
*/
@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback {


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
        rabbitTemplate.setConnectionFactory(connectionFactory);
    }


    /*  参数详解：
     *      correlationData: 保存回调消息的ID及相关信息，发送消息的时候填写
     *      ack:        交换机是否接收到消息
     *      cause:      消息接收失败原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        String id = correlationData.getId()!=null ? correlationData.getId() : "";

        if (ack){
            log.info("{}交换机接收到了消息,消息ID为:{}", correlationData.getReturned().getExchange(),id);
        }else{
            log.warn("{}交换机接收ID为{}的消息失败,原因:{}",
                    correlationData.getReturned().getExchange(),id, cause
            );
        }

    }

}
