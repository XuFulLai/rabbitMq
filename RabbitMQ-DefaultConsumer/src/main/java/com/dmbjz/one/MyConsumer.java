package com.dmbjz.one;


import com.dmbjz.consumer.DmbjzConsumer;
import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

public class MyConsumer {

      public static final String QUEUE_NAME = "MyQueue";

      public static void main(String[] args) throws IOException {

            Connection connection = RabbitUtils.getConnection();
            Channel channel = connection.createChannel();
            channel.basicConsume(QUEUE_NAME,new DmbjzConsumer(channel));

      }

}
