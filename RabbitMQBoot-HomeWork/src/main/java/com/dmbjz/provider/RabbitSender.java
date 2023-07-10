package com.dmbjz.provider;


import com.alibaba.fastjson.JSON;
import com.dmbjz.entity.Professor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


@RestController
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /* 普通消息请求 */
    @RequestMapping("send/{message}")
    private void send(@PathVariable("message")String message){

        MessageProperties properties = new MessageProperties();
        properties.setAppId(UUID.randomUUID().toString());
        properties.setContentEncoding("UTF-8");
        properties.setContentType("application/text");

        Message msg = new Message(message.getBytes(StandardCharsets.UTF_8),properties);

        /*设置回调消息*/
        ReturnedMessage reMsg = new ReturnedMessage(msg,
                200,"这是send传递的消息",
                "SchoolExchange","homework.match");
        CorrelationData correlationData = new CorrelationData();
        correlationData.setReturned(reMsg);

        rabbitTemplate.convertAndSend("SchoolExchange","homework.match",msg,correlationData);

    }



    /* 发送JSON消息使用实体类接收 */
    @RequestMapping("send2")
    private void teacherSend(){

        MessageProperties properties = new MessageProperties();
        properties.setAppId(UUID.randomUUID().toString());
        properties.setContentEncoding("UTF-8");
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);

        for(int i = 0; i < 10; i++) {

            Professor professor = new Professor().setId(UUID.randomUUID().toString()).setName("教授"+i).setType("语文").setPhone(1234567890);
            String json = JSON.toJSONString(professor);
            Message message = new Message(json.getBytes(StandardCharsets.UTF_8),properties);

            /* 回调消息 */
            ReturnedMessage reMsg = new ReturnedMessage(message,
                    200,"这是send2传递的消息",
                    "TeacherExchange","Teacher.info");
            CorrelationData correlationData = new CorrelationData();
            correlationData.setReturned(reMsg);

            rabbitTemplate.convertAndSend("TeacherExchange","Teacher.info",message,correlationData);

        }

    }


    /* 发送JSON消息使用不同转换器接收 */
    @RequestMapping("send3")
    private void moreTypeSend() throws IOException {

        MessageProperties properties = new MessageProperties();
        properties.setAppId(UUID.randomUUID().toString());
        properties.setContentEncoding("UTF-8");


        /* 发送图片消息 */
        byte[] body = Files.readAllBytes(Paths.get("E:/图片/头像", "ludashi.png"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("image");
        Message message = new Message(body, messageProperties);

        /* 回调消息 */
        ReturnedMessage reMsg = new ReturnedMessage(message,
                200,"这是send2传递的消息",
                "TeacherExchange","Teacher.info");
        CorrelationData correlationData = new CorrelationData();
        correlationData.setReturned(reMsg);


        rabbitTemplate.convertAndSend("TeacherExchange","File.picinfo",message,correlationData);

        /* 发送PDF消息 */
        byte[] body2 = Files.readAllBytes(Paths.get("E:/", "rabbitmq.pdf"));
        MessageProperties messageProperties2 = new MessageProperties();
        messageProperties2.setContentType("application/pdf");
        Message message2 = new Message(body2, messageProperties2);
        rabbitTemplate.convertAndSend("TeacherExchange", "File.pdfinfo", message2,correlationData);


    }


}
