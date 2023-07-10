package com.dmbjz.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/*HelloWord模型 消费者案例*/
public class Consumer {

    public static final String QUEUE_NAME = "hello";  // 队列名称

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("140.210.201.235");           // 设置MQ所在机器IP进行连接
        factory.setPort(5672);                        // 指定MQ服务端口
        factory.setVirtualHost("study");              // 指定使用的VirtualHost
        factory.setUsername("heima");                 // 指定MQ账号名
        factory.setPassword("heima");                   // 指定MQ密码

        Connection connection = factory.newConnection();    // 创建连接
        Channel channel = connection.createChannel();       // 创建信道

        /*消费者成功消费时的回调接口，这里为打印获取到的消息*/
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(new String(message.getBody()));
        };

        /*消费者取消消费的回调*/
        CancelCallback callback = consumerTag -> {
            System.out.println("消息者取消消费接口回调逻辑");
        };

        /*  消费消息
         * 参数1 ： 消费队列的名称
         * 参数2 ： 消息的自动确认机制(一获得消息就通知 MQ 消息已被消费)  true打开，false关闭 (接收到消息并消费后也不通知 MQ ，常用)
         * 参数3 ： 消费者成功消费时的回调接口
         * 参数4 ： 消费者取消消费的回调
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, callback);

        System.out.println("消费者执行完毕");

    }

}
