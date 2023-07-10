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
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
public class ProviderController {


    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable String message){

        log.info("发送消息内容:{}",message);

        /*设置能正常收到的消息*/
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId("1");
        ReturnedMessage msg = new ReturnedMessage(
                new Message(message.getBytes(StandardCharsets.UTF_8)),
                200,
                "这是能被成功发送的消息",
                RabbitMQConfig.CONFIRM_EXCHANGE_NAME,
                "key1");
        correlationData.setReturned(msg);

        rabbitTemplate.convertAndSend(RabbitMQConfig.CONFIRM_EXCHANGE_NAME, "key1", message.getBytes(StandardCharsets.UTF_8), correlationData);


        /*设置不能被正常收到的消息 - 交换机不存在 */
        CorrelationData correlationData2 = new CorrelationData();
        String msg2 = message + "No Exchange";
        ReturnedMessage errMsg = new ReturnedMessage(
                new Message(msg2.getBytes(StandardCharsets.UTF_8)),
                200,
                "这个消息交换机不存在",
                RabbitMQConfig.CONFIRM_EXCHANGE_NAME+"404",
                "key1");
        correlationData2.setId("2");
        correlationData2.setReturned(errMsg);

        rabbitTemplate.convertAndSend(RabbitMQConfig.CONFIRM_EXCHANGE_NAME+"404", "key1",
                msg2.getBytes(StandardCharsets.UTF_8), correlationData2);


        /*设置不能被正常收到的消息 - 无法被发送到队列(错误的路由Key) */
        CorrelationData correlationData3 = new CorrelationData();
        String msg3 = message + "No Roting Key";
        ReturnedMessage errMsg2 = new ReturnedMessage(
                new Message(message.getBytes(StandardCharsets.UTF_8)),
                200,
                "这个消息路由Key不对",
                RabbitMQConfig.CONFIRM_EXCHANGE_NAME,
                "key404");
        correlationData3.setId("3");
        correlationData3.setReturned(errMsg2);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CONFIRM_EXCHANGE_NAME, "key404",
                msg3.getBytes(StandardCharsets.UTF_8), correlationData3);



    }


}
