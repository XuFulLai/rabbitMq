package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.*;

/*Fanout模式 学生B(消费者)案例*/
public class StudentB {

    private static final String EXCHANGE_NAME = "FanoutExchange";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        /*创建交换机，由于生产者已经创建该交换机，如果生产者先执行，这行实际可以省略不写
         * 参数一：交换机名称
         * 参数二：交换机类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        /*
         * 使用交换机后一般采用临时队列方式进行绑定
         * 临时队列方式：创建队列 > 获取队列
         * 队列绑定参数：
         *       参数1：队列名称
         *       参数2：交换机名称
         *       参数3：路由key（fanout没有该参数，null）
         * */
        String tempQueue = channel.queueDeclare().getQueue();
        channel.queueBind(tempQueue,EXCHANGE_NAME,"");                                            //绑定队列

        /*消费者成功消费时的回调接口，这里为打印获取到的消息*/
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("学生B接收到的消息:"+new String(message.getBody()));
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);        //手动应答
        };

        /*消费者取消消费的回调*/
        CancelCallback callback = consumerTag -> {
            System.out.println("学生B取消消费接口回调逻辑");
        };

        channel.basicConsume(tempQueue,false,deliverCallback,callback);      //消费信息

    }

}
