package com.dmbjz.one;


import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*消息重新入队 生产者代码*/
public class MyProvider {

    public static final String QUEUE_NAME = "MyQueue";
    public static final String EXCHANGE_NAME = "MyExchange";

    public static void main(String[] args) throws IOException {

        Connection connection = RabbitUtils.getConnection();            //创建连接
        Channel channel = connection.createChannel();                   //创建信道

        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"hello");

        Scanner scanner = new Scanner(System.in);       //从控制台输入消息内容
        while (scanner.hasNext()){
            String message = scanner.next();
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    .appId("12345")
                    .contentType("app   lication/text")
                    .build();
            channel.basicPublish(EXCHANGE_NAME,"hello",properties ,message.getBytes(StandardCharsets.UTF_8));      //发送消息并将消息持久化到磁盘
            System.out.println("消息发送完成:" + message);
        }

    }

}
