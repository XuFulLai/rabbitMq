package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*Fanout模式 生产者案例*/

/**
 * 又称广播模式，生产者将消息发送到交换机，消费者将队列绑定到交换机来获取信息，该模式下指定RoutingKey 无意义
 */
public class Provider {

    private static final String EXCHANGE_NAME = "FanoutExchange";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        /*创建交换机
        * 参数一：交换机名称
        * 参数二：交换机类型
        */
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        Scanner scanner = new Scanner(System.in);       //从控制台输入消息内容
        while (scanner.hasNext()){
            String message = scanner.next();

            /*  交换机&队列设置（指定消息使用的交换机和队列）
             * 参数1： exchange交换机名称（简单队列无交换机，这里不写）
             * 参数2： 有交换机就是路由key。没有交换机就是队列名称
             * 参数3： 传递消息的额外设置 (设置消息是否持久化）  MessageProperties.PERSISTENT_TEXT_PLAIN设置消息持久化
             * 参数4： 消息具体内容（要为 Byte类型）
             */
            channel.basicPublish(EXCHANGE_NAME,""
                    ,MessageProperties.PERSISTENT_TEXT_PLAIN
                    ,message.getBytes(StandardCharsets.UTF_8));
            System.out.println("消息发送完成:" + message);
        }

    }


}
