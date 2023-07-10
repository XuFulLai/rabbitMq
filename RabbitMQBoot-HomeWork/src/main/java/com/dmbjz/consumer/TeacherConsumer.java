package com.dmbjz.consumer;


import com.dmbjz.entity.Professor;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


@Component
@Slf4j
public class TeacherConsumer {


    /*  使用Java对象接收消息并手动ACK需要的注解：
    *       @Payload 注入消息体到一个JavaBean中
    *       @Headers 注入所有消息头到一个Map中
    *       @Header 注入消息头的单个属性
    */
    @RabbitListener(queues = "TeacherQueue",containerFactory = "rabbitListenerContainerFactory")
    public void professorMessage(@Payload Professor professor, @Headers Map<String,Object> headers, Channel channel) throws IOException {

        log.info("教授消息: " + professor );
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        channel.basicAck(deliveryTag,false);        //手动ACK

    }



}
