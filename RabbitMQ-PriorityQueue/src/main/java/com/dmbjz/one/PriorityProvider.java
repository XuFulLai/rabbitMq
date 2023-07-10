package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/* 生产者代码 */
public class PriorityProvider {

    private static final String EXCHANGE_NAME = "FanoutExchange";          //交换机名称

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);            //声明交换机


        /* 对队列设置优先级为10,值应该为 0~255 之间 */
        Map<String,Object> map = new HashMap<>(1);
        map.put("x-max-priority",10);
        channel.queueDeclare("KeFuQueue",false,false,false,map);
        channel.queueBind("KeFuQueue",EXCHANGE_NAME,"",null);

        /* 消息发送 */
        for (int i = 0; i < 10; i++) {

            String message = "第"+i+"条消息";
            if( i%5==0 ){
                AMQP.BasicProperties properties = new AMQP.BasicProperties()
                        .builder().priority(5).build();         //设置消息优先级为5

                channel.basicPublish(EXCHANGE_NAME,"",properties,message.getBytes(StandardCharsets.UTF_8));

            }else{
                channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes(StandardCharsets.UTF_8));
            }

        }

        System.out.println("消息全部发送");

    }


}
