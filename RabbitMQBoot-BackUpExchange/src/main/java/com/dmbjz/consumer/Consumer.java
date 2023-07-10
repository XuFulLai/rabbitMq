package com.dmbjz.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/* 消费者代码 */
@Component
@Slf4j
public class Consumer {

    @RabbitListener(queues = "confirm.queue")
    public void receiveConfirmMessage(Message message, Channel channel){

        log.info(" confirm.queue 接收到的消息:"+new String(message.getBody()));

    }


    @RabbitListener(queues = "warning.queue")
    public void warnQueueMessage(Message message, Channel channel){

        log.info(" warning.queue 接收到的消息:"+new String(message.getBody()));

    }


}
