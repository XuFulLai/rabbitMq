package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.nio.charset.StandardCharsets;

/*Direct模式 生产者代码*/

/**
 * Direct模式：
 * 生产消息时指定 routingKey ，交换机根据不同的 routingKey 将消息进行转发到对应的 channel 上供不同的消费者进行消费
 * @author xufulai
 */
public class Provider {

    private static final String EXCHANGE_NAME = "DirectExchange";          //交换机名称
    private static final String VipKey = "Vip";                 //普通VIP
    private static final String NormalKey = "Normal";           //普通用户
    private static final String SuperVipKey = "SuperVip";       //超级VIP

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);            //声明交换机

        /*模拟不同会员等级接收到的不同消息内容*/
        for (int i = 0; i < 9; i++) {
            String message = "当前是待发送的消息，序号:"+i;
            if(i%3==0){
                channel.basicPublish(EXCHANGE_NAME,SuperVipKey,null,message.getBytes(StandardCharsets.UTF_8));  //发送超级VIP消息
            }
            if(i%3==1){
                channel.basicPublish(EXCHANGE_NAME,VipKey,null,message.getBytes(StandardCharsets.UTF_8));      //发送VIP消息
            }
            if(i%3==2){
                channel.basicPublish(EXCHANGE_NAME,NormalKey,null,message.getBytes(StandardCharsets.UTF_8));   //发送通用消息
            }
            System.out.println("消息发送: " + message);
        }


    }


}
