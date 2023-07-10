package com.dmbjz.one;


import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Provider {

    public static final String QUEUE_NAME = "SlowQueue";
    public static final String EXCHANGE_NAME = "FastExchange";

    public static void main(String[] args) throws IOException {

        Connection connection = RabbitUtils.getConnection();            //创建连接
        Channel channel = connection.createChannel();                   //创建信道

        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"hello");


        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5,10,
                100, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());

        for (int i = 0; i < 2000; i++) {
            threadPool.execute(()->{
                String message = Thread.currentThread().getName();
                try {
                    channel.basicPublish(EXCHANGE_NAME,"hello",null ,message.getBytes(StandardCharsets.UTF_8));      //发送消息并将消息持久化到磁盘
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        threadPool.shutdown();
        while (!threadPool.isTerminated()) {

        }
        System.out.println("所有消息发送完成");

    }



}
