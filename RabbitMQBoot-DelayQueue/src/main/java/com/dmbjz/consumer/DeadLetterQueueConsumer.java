package com.dmbjz.consumer;


import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/* 队列TTL消费者 */
@Component
@Slf4j
public class DeadLetterQueueConsumer {


    @RabbitListener(queues = "QD")
    public void receiveD(Message message, Channel channel)throws Exception{

        String msg = new String(message.getBody());
        log.info("当前时间:{},收到死信队列的消息:{}",new Date(),msg);

    }


}
