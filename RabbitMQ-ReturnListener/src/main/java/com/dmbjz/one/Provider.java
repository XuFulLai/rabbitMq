package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/*生产者代码*/
public class Provider {

    private static final String EXCHANGE_NAME = "DirectExchange";          //交换机名称
    private static final String VipKey = "Vip";                 //普通VIP
    private static final String NOKEY = "Nokey";           //普通用户


    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);            //声明交换机


        /*添加消息ReturnListener*/
        channel.addReturnListener(new ReturnListener() {


            /*参数详解：
            *   replyCode:  路由是否成功的响应码
            *   replyText:  文本说明
            *   exchange：   具体路由的交换机
            *   routingKey： 路由Key
            *   properties： 消息配置，可指定消息具体参数（SpringBoot章节详细讲解）
            *
            * */
            @Override
            public void handleReturn(int replyCode, String replyText,
                                     String exchange, String routingKey,
                                     AMQP.BasicProperties properties, byte[] body) throws IOException {

                System.out.println("ReturnListener获到不可路由消息!");
                System.out.println("replyCode: "+replyCode);
                System.out.println("replyText: "+replyText);
                System.out.println("exchange: "+exchange);
                System.out.println("routingKey: "+routingKey);
                System.out.println("properties: "+properties);
                System.out.println("body: "+new String(body));

            }
        });

        /*要指定 mandatory 为 true，默认的 false为 RabbitMQ自动删除不可路由消息 */
        channel.basicPublish(EXCHANGE_NAME,VipKey,true,null,"这是一条测试消息".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(EXCHANGE_NAME,NOKEY,true,null,"这是一条测试消息".getBytes(StandardCharsets.UTF_8));


    }


}
