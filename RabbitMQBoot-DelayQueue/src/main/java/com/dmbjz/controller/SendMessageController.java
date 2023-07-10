package com.dmbjz.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/* 生产者发送消息Controller */
@RestController
@RequestMapping("/ttl")
@Slf4j
public class SendMessageController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*队列TTL案例*/
    @RequestMapping("/sendMessage/{message}")
    public void sendMsg(@PathVariable String message){

        log.info("当前时间:{},发送一条信息给两个TTL队列,消息内容:{}",new Date(),message);
        rabbitTemplate.convertAndSend("X","XA",message.getBytes(StandardCharsets.UTF_8));
        rabbitTemplate.convertAndSend("X","XB",message.getBytes(StandardCharsets.UTF_8));

    }

    /*消息TTL案例*/
    @RequestMapping("/sendMessagExpira/{message}/{time}")
    public void sendMsgExpira(@PathVariable String message,
                              @PathVariable String time){

        MessageProperties properties = new MessageProperties();
        properties.setExpiration(time);
        Message msg = new Message(message.getBytes(StandardCharsets.UTF_8),properties);

        log.info("当前时间:{},发送具有过期时间为{}毫秒的信息给QC队列,消息内容:{}",new Date(),time,message);
        rabbitTemplate.convertAndSend("X","XC",msg);

    }


    /*延时插件案例*/
    @RequestMapping("/sendMessagPlugin/{message}/{time}")
    public void sendMsgPlugin(@PathVariable String message,
                              @PathVariable Integer time){

        MessageProperties properties = new MessageProperties();
        properties.setDelay(time);      //设置延时时间
        Message msg = new Message(message.getBytes(StandardCharsets.UTF_8),properties);

        log.info("当前时间:{},发送具有过期时间为{}毫秒的信息给延时插件队列,消息内容:{}",new Date(),time,message);
        rabbitTemplate.convertAndSend("delayed.exchange","delayed.routingkey",msg);

    }


}
