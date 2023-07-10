package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/*消息重新入队演示  消费者*/
public class Consumer {

    public static final String QUEUE_NAME = "SlowQueue";

    public static void main(String[] args) throws IOException {

        Connection connection = RabbitUtils.getConnection();

        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);

        /*消费者成功消费回调逻辑*/
        DeliverCallback deliverCallback = (consumerTag, message) -> {

            try {
                TimeUnit.SECONDS.sleep(1);  //模拟实际业务操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("消费者接收到的信息为:"+new String(message.getBody()));

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


        /*
         * 参数一：单条消息大小限制，一般为0（不限制）
         * 参数二：一次性消费的消息数量。会告诉 RabbitMQ 不要同时给一个消费者推送多于 N 个消息，即一旦有 N 个消息还没有 ack，则该 consumer 将 block 掉，直到积累的消息数 ack 到能接受新消息
         *     在 no_ask=false 的情况下才生效，即在自动应答的情况下这两个值是不生效的。
         *     一般设置为 1
         * 参数三：限流设置应用于 channel(true) 还是 consumer (false)
         *     通常设置为false，因为 channel 级别限流RabbitMQ当前版本尚未实现且多数情况下是共用一个 channel
         */
        channel.basicQos(0,10,false);                         //设置限流操作
        channel.basicConsume(QUEUE_NAME,false,deliverCallback,cancelCallback);      //手动应答消费消息


    }


}
