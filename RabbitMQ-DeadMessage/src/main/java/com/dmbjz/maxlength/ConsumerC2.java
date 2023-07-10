package com.dmbjz.maxlength;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

/* 死信队列 队列达到最大长度案例 消费者C2 */
public class ConsumerC2 {

    private static final String DEAD_QUEUE_NAME = "dead-queue";    //死信队列名称

    public static void main(String[] args) throws IOException {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        DeliverCallback successBack = (consumerTag, message) -> {
            System.out.println("C1用户接收到的信息为:"+new String(message.getBody()));
        };

        CancelCallback cnaelBack = a->{
            System.out.println("C1用户进行取消消费操作!");
        };

        channel.basicConsume(DEAD_QUEUE_NAME,true,successBack,cnaelBack);

    }


}
