package com.dmbjz.controller;

import com.dmbjz.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/* 生产者发送消息 */
@Slf4j
@RestController
@RequestMapping("/ReturnCallBack")
public class ProviderController {


    @Autowired
    private RabbitTemplate rabbitTemplate;


    @RequestMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable("message") String info){

        log.info("发送的消息为:"+info);

        /*设置回调的消息*/
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId("1"); //默认为UUID
        ReturnedMessage msg = new ReturnedMessage(
                new Message(info.getBytes(StandardCharsets.UTF_8)),
                1,
                "1",
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.Routing_Key+"112233");
        correlationData.setReturned(msg);


        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.Routing_Key+"112233",
                info.getBytes(StandardCharsets.UTF_8),
                correlationData
        );

    }


}
