package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.*;

/*Topic模型 消费者Q1案例*/
public class ConsumerQ1 {

    private static final String EXCHANGE_NAME = "TopicExchange";          //交换机名称
    private static final String ORANGE_KEY= "*.orange.*";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        /*
         *参数一:  交换机名称
         *参数二:  交换机类型
         *参数三:  是否持久化
         */
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC,false);  //声明交换机，由于生产者已经创建该交换机，如果生产者先执行，这行实际可以省略不写
        String queue = channel.queueDeclare().getQueue();                   //创建临时队列
        channel.queueBind(queue,EXCHANGE_NAME,ORANGE_KEY);                  //绑定队列和交换机

        /*消费者成功消费回调逻辑*/
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("Q1用户接收到的信息为:"+new String(message.getBody()));
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);     //手动消息应答
        };

        /*消费者取消消费回调逻辑*/
        CancelCallback cancelCallback = a->{
            System.out.println("Q1用户进行取消消费操作!");
        };

        channel.basicConsume(queue,false,deliverCallback,cancelCallback);

    }


}
