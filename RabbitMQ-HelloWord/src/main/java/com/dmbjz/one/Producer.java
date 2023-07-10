package com.dmbjz.one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/*HelloWord模型 生产者代码*/
public class Producer {

    public static final String QUEUE_NAME = "hello"; // 队列名称

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("140.210.201.235");           // 设置MQ所在机器IP进行连接
        factory.setPort(5672);                        // 指定MQ服务端口
        factory.setVirtualHost("study");              // 指定使用的VirtualHost
        factory.setUsername("heima");                 // 指定MQ账号名
        factory.setPassword("heima");                   // 指定MQ密码

        Connection connection = factory.newConnection();    // 创建连接
        Channel channel = connection.createChannel();       // 创建信道

        /*  队列设置（创建队列)
         *参数1：队列名称，名称不存在就自动创建
         *参数2：定义队列是否持久化（重启MQ后是队列否存在）,true开启，false关闭
         *参数3：exclusive 是否独占队列（设置是否只能有一个消费者使用），true独占，false非独占
         *参数4：autoelete 是否在消费完成后是否自动删除队列 ，true删除,false不删除
         *参数5：额外附加参数
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        String message = "Hello hhh11111111h11111hh";            // 需要发送的消息

        /*  交换机&队列设置（指定消息使用的交换机和队列）
         * 参数1： exchange交换机名称（简单队列无交换机，这里不写）
         * 参数2： 有交换机就是路由key。没有交换机就是队列名称，意为往该队列里存放消息
         * 参数3： 传递消息的额外设置 (设置消息是否持久化）  MessageProperties.PERSISTENT_TEXT_PLAIN设置消息持久化
         * 参数4： 消息具体内容（要为 Byte类型）
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));

        /*关闭资源*/
        channel.close();
        connection.close();

        System.out.println("消息生产完毕");

    }

}
