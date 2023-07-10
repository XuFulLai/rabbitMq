package com.dmbjz.noack;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* 死信队列 队列达到最大长度案例 消费者C1 */
public class ConsumerC1 {


    private static final String EXCHANGE_NAME = "normal_exchange";              //正常交换机名称
    private static final String DEAD_EXCHANGE_NAME = "dead_exchange";           //死信队列交换机名称

    private static final String KEY = "zhangsan";        //普通队列 RoutingKey
    private static final String DEAD_KEY = "lisi";       //死信队列 RoutingKey

    private static final String QUEUE_NAME = "normal3-queue";       //普通队列名称
    private static final String DEAD_QUEUE_NAME = "dead-queue";    //死信队列名称


    public static void main(String[] args) throws IOException {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();


        /*声明死信和普通交换机，正常交换机已被生产者声明，实际可以省略第一行代码*/
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);


        /*创建队列
        * 通过额外参数实现什么情况下转发到死信队列 ？,key都是固定的
        *   1、TTL过期时间设置(一般由生产者指定)
        *   2、死信交换机的名称
        *   3、死信交换机的RoutingKey
        * */
        Map<String,Object> arguments = new HashMap<>(8);
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE_NAME);     //死信交换机的名称
        arguments.put("x-dead-letter-routing-key",DEAD_KEY);            //死信交换机的RoutingKey

        channel.queueDeclare(QUEUE_NAME,false,false,false,arguments);
        channel.queueDeclare(DEAD_QUEUE_NAME,false,false,false,null);


        /*绑定队列*/
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,KEY);
        channel.queueBind(DEAD_QUEUE_NAME,DEAD_EXCHANGE_NAME,DEAD_KEY);


        DeliverCallback successBack = (consumerTag, message) -> {

            String info = new String(message.getBody(),"UTF-8");
            if(info.equals("INFO 5")){
                System.out.println("C1用户拒绝的信息为:"+new String(message.getBody()));
                /* requeue 设置为 false 代表拒绝重新入队 该队列如果配置了死信交换机将发送到死信队列中,未配置则进行丢弃操作*/
                channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
            }else{
                System.out.println("C1用户接收到的信息为:"+new String(message.getBody()));
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            }


        };

        CancelCallback cnaelBack = a->{
            System.out.println("C1用户进行取消消费操作!");
        };

        channel.basicConsume(QUEUE_NAME,false,successBack,cnaelBack);


    }


}
