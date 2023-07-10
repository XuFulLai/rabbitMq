package com.dmbjz.one;


import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*WorkQueue模型 生产者代码*/
public class WorkProvider {

    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException {

        Connection connection = RabbitUtils.getConnection();            //创建连接

        Channel channel = connection.createChannel();                   //创建信道

        /*  队列设置（创建队列)
         *参数1：队列名称，名称不存在就自动创建
         *参数2：定义队列是否持久化（重启MQ后是队列否存在）,true开启，false关闭
         *参数3：exclusive 是否独占队列（设置是否只能有一个消费者使用），true独占，false非独占
         *参数4：autoelete 是否在消费完成后是否自动删除队列 ，true删除,false不删除
         *参数5：额外附加参数
         */
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);


        Scanner scanner = new Scanner(System.in);       //从控制台输入消息内容
        while (scanner.hasNext()){
            String message = scanner.next();

            /*  交换机&队列设置（指定消息使用的交换机和队列）
             * 参数1： exchange交换机名称（简单队列无交换机，这里不写）
             * 参数2： 有交换机就是路由key。没有交换机就是队列名称，意为往该队列里存放消息
             * 参数3： 传递消息的额外设置 (设置消息是否持久化）  MessageProperties.PERSISTENT_TEXT_PLAIN设置消息持久化
             * 参数4： 消息具体内容（要为 Byte类型）
             */
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes(StandardCharsets.UTF_8));
            System.out.println("消息发送完成:" + message);
        }


    }



}
