package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/*消息重新入队演示  消费者A*/
public class ConsumerA {


    public static final String ASK_QUEUE_NAME = "ASK_QUEUE";

    public static void main(String[] args) throws IOException {

        Connection connection = RabbitUtils.getConnection();

        Channel channel = connection.createChannel();

        /*消费者成功消费回调逻辑*/
        DeliverCallback deliverCallback = (consumerTag, message) -> {

            System.out.println("消费者A对消息进行消费!");
            try {
                TimeUnit.SECONDS.sleep(1);  //模拟实际业务操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("消费者A接收到的信息为:"+new String(message.getBody()));

            /*
            * 参数一：  消息标记tag
            * 参数二：  是否批量消费消息（true为应答该队列中所有的消息，false为只应答接收到的消息）
            * */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);     //手动消息应答


        };

        /*消费者取消消费回调逻辑*/
        CancelCallback cancelCallback =  a->{
            System.out.println("消费者A进行取消消费操作!");
        };

        /* channel.basicQos(1);            //设置不公平分发  */
        channel.basicQos(5);            //设置预取值为 2
        channel.basicConsume(ASK_QUEUE_NAME,false,deliverCallback,cancelCallback);      //消费消息


    }


}
