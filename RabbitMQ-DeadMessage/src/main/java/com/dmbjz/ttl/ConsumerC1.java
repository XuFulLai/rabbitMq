package com.dmbjz.ttl;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* 死信队列TTL案例 消费者C1 */
public class ConsumerC1 {


    private static final String EXCHANGE_NAME = "normal_exchange";              //正常交换机名称
    private static final String DEAD_EXCHANGE_NAME = "dead_exchange";           //死信队列交换机名称

    private static final String KEY = "zhangsan";        //普通队列 RoutingKey
    private static final String DEAD_KEY = "lisi";       //死信队列 RoutingKey

    private static final String QUEUE_NAME = "normal-queue";       //普通队列名称
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
        // arguments.put("x-dead-letter-ttl",10000);   指定消息的有效时间为10秒（一般为生产者指定）
        channel.queueDeclare(QUEUE_NAME,false,false,false,arguments);
        channel.queueDeclare(DEAD_QUEUE_NAME,false,false,false,null);


        /*绑定队列*/
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,KEY);
        channel.queueBind(DEAD_QUEUE_NAME,DEAD_EXCHANGE_NAME,DEAD_KEY);



        DeliverCallback successBack = (consumerTag, message) -> {
            System.out.println("C1用户接收到的信息为:"+new String(message.getBody()));
        };

        CancelCallback cnaelBack = a->{
            System.out.println("C1用户进行取消消费操作!");
        };

        channel.basicConsume(QUEUE_NAME,true,successBack,cnaelBack);


    }



}
