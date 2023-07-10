package com.dmbjz.controller;


import com.dmbjz.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;


/*生产者代码*/
@Slf4j
@RestController
@RequestMapping("/confirm")
public class ProducerController {


    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable String message){

        log.info("发送消息内容:{}",message);

        /*设置回调的消息*/
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId("1");     //消息ID

        ReturnedMessage msg = new ReturnedMessage(
                new Message(message.getBytes(StandardCharsets.UTF_8)),
                1,
                "1",
                RabbitMQConfig.CONFIRM_EXCHANGE_NAME,
                RabbitMQConfig.CONFIRM_ROUTING_KEY);        //设置消息、状态码、说明、交换机、路由Key
        correlationData.setReturned(msg);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CONFIRM_EXCHANGE_NAME,
                RabbitMQConfig.CONFIRM_ROUTING_KEY,
                message.getBytes(StandardCharsets.UTF_8),
                correlationData
        );

    }



}
