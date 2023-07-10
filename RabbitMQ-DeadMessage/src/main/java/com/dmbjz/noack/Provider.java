package com.dmbjz.noack;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.nio.charset.StandardCharsets;

/* 死信队列 队列达到最大长度案例 生产者 */
public class Provider {

    private static final String EXCHANGE_NAME = "normal_exchange";              //正常交换机名称
    private static final String KEY = "zhangsan";        //普通队列 RoutingKey

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,BuiltinExchangeType.DIRECT);  //声明交换机

        /*循环消息发送*/
        for(int i = 1; i < 11; i++) {
            String message = "INFO " + i;
            channel.basicPublish(EXCHANGE_NAME,KEY,null,message.getBytes(StandardCharsets.UTF_8));  //发送超级VIP消息
        }

    }

}
