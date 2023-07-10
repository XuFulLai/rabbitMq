package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.*;

/*Direct模式 消费者代码*/
public class VipConsumer {

    private static final String EXCHANGE_NAME = "DirectExchange";          //交换机名称
    private static final String NormalKey = "Normal";           //普通用户
    private static final String VipKey = "Vip";                 //普通VIP

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);            //声明交换机，由于生产者已经创建该交换机，如果生产者先执行，这行实际可以省略不写
        String queue = channel.queueDeclare().getQueue();                //创建临时队列
        channel.queueBind(queue,EXCHANGE_NAME,VipKey);                   //绑定队列和交换机
        channel.queueBind(queue,EXCHANGE_NAME,NormalKey);                //绑定队列和交换机

        /*消费者成功消费回调逻辑*/
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("Vip用户接收到的信息为:"+new String(message.getBody()));
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);     //手动消息应答
        };

        /*消费者取消消费回调逻辑*/
        CancelCallback cancelCallback = a->{
            System.out.println("Vip用户进行取消消费操作!");
        };

        channel.basicConsume(queue,false,deliverCallback,cancelCallback);

    }


}
