package com.dmbjz.one;


import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*消息重新入队 生产者代码*/
public class WorkProvider {

    public static final String ASK_QUEUE_NAME = "ASK_QUEUE";

    public static void main(String[] args) throws IOException {

        Connection connection = RabbitUtils.getConnection();            //创建连接
        Channel channel = connection.createChannel();                   //创建信道

        channel.queueDeclare(ASK_QUEUE_NAME,true,false,false,null);     //初始化信道

        Scanner scanner = new Scanner(System.in);       //从控制台输入消息内容
        while (scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish("",ASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN
                    ,message.getBytes(StandardCharsets.UTF_8));      //发送消息并将消息持久化到磁盘
            System.out.println("消息发送完成:" + message);
        }

    }

}
