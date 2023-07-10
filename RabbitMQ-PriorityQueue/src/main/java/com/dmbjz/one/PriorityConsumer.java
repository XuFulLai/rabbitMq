package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.*;

/*Direct模式 消费者代码*/
public class PriorityConsumer {

    private static final String QUEUE_NAME = "KeFuQueue";          //队列名称

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();


        /*消费者成功消费回调逻辑*/
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("接收到的信息为:"+new String(message.getBody()));
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);     //手动消息应答
        };

        /*消费者取消消费回调逻辑*/
        CancelCallback cancelCallback = a->{
            System.out.println("进行取消消费操作!");
        };

        channel.basicConsume(QUEUE_NAME,false,deliverCallback,cancelCallback);

    }


}
