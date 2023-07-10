package com.dmbjz.ttl;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

/* 死信队列TTL案例 消费者C2 */
public class ConsumerC2 {

    private static final String DEAD_EXCHANGE_NAME = "dead_exchange";           //死信队列交换机名称
    private static final String DEAD_KEY = "lisi";       //死信队列 RoutingKey
    private static final String DEAD_QUEUE_NAME = "dead-queue";    //死信队列名称


    public static void main(String[] args) throws IOException {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();


        /*声明队列和普通交换机并进行绑定,由于消费者C1已经声明过了，这里实际可以省略这三行代码*/
        channel.exchangeDeclare(DEAD_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(DEAD_QUEUE_NAME,false,false,false,null);
        channel.queueBind(DEAD_QUEUE_NAME,DEAD_EXCHANGE_NAME,DEAD_KEY);


        DeliverCallback successBack = (consumerTag, message) -> {
            System.out.println("C1用户接收到的信息为:"+new String(message.getBody()));
        };

        CancelCallback cnaelBack = a->{
            System.out.println("C1用户进行取消消费操作!");
        };

        channel.basicConsume(DEAD_QUEUE_NAME,true,successBack,cnaelBack);


    }


}
