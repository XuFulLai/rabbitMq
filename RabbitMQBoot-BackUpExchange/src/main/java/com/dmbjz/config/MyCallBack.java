package com.dmbjz.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/* 实现回调方法 */
@Component
@Slf4j
public class MyCallBack implements RabbitTemplate.ReturnsCallback,RabbitTemplate.ConfirmCallback {


    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ConnectionFactory connectionFactory;

    @PostConstruct
    public void init(){
        rabbitTemplate.setReturnsCallback(this);
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setConnectionFactory(connectionFactory);
    }


    /*消息发送到交换机的回调方法*/
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        ReturnedMessage returned = correlationData.getReturned();   //获取返回消息

        if (ack){
            log.info("当前消息成功发送给{}交换机,当前消息的路由key是:{} ,消息:{}",
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    new String(returned.getMessage().getBody()) );
        }else{
            log.warn("当前消息发送给{}交换机失败,消息内容:{},消息说明:{},错误原因:{}",
                    returned.getExchange(),
                    new String(returned.getMessage().getBody()),
                    returned.getReplyText(),
                    cause );
        }

    }


    /*消息无法路由到队列的处理方法，当有设置备份交换机时会被备份交换机处理方法给覆盖*/
    @Override
    public void returnedMessage(ReturnedMessage returned) {

        log.warn("================消息无法发送到队列触发回调开始================");
        log.warn("消息相应码 : "+returned.getReplyCode());
        log.warn("消息主体 message : "+returned.getMessage());
        log.warn("描述："+returned.getReplyText());
        log.warn("消息使用的交换器 exchange : "+ returned.getMessage());
        log.warn("消息使用的路由键 routiAng : "+ returned.getRoutingKey());
        log.warn("================消息无法发送到队列触发回调结束================");

    }



}
