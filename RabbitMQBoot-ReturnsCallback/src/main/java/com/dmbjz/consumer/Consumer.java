package com.dmbjz.consumer;

import com.dmbjz.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/* 消费者监听 */
@Component
@Slf4j
public class Consumer {


    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumerMessage(Message message,Channel channel){

        System.out.println("获取到的消息: "+new String(message.getBody()));

    }


}
