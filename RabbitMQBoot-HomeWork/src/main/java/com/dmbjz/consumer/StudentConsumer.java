package com.dmbjz.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class StudentConsumer {

    /* 使用Spring封装的Message而不是RabbitMQ的Message获取消息 */
    @RabbitListener(
            containerFactory = "rabbitListenerContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = "StudentQueue",durable = "true"),
                    exchange = @Exchange(value = "SchoolExchange",durable = "true",type = "topic"),
                    key = "homework.#"
            )
    )
    public void mathMessage(Message message, Channel channel) throws IOException, InterruptedException {

        log.info("获取到的作业消息: "+ new String((byte[]) message.getPayload()));

        TimeUnit.SECONDS.sleep(1);      //模拟消息处理消耗时间
        /* 消息应答 */
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        channel.basicNack(deliveryTag,false,false);

    }



}
