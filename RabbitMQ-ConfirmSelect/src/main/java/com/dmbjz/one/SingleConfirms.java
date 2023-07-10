package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


/**
 * @author xufulai
 * 单确认模式：
 * 只有当消息被确认发布，后续的消息才能继续发布。缺点是运行效率低，每秒只能确认百条消息
 */ /*消息应答 单个确认案例*/
public class SingleConfirms {

    private static final int MESSAGE_COUNT = 1000;
    private static final String QUEUE_NAME = UUID.randomUUID().toString();

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);		//队列初始化
        channel.confirmSelect();                            //设置消息确认
        long startTime = System.currentTimeMillis();        //开始时间
        for (int i = 0; i < MESSAGE_COUNT; ++i) {

            String message = "当前是" + i + "条消息";
            System.out.println("队列名称:" + QUEUE_NAME);
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes(StandardCharsets.UTF_8));     //消息发送

            boolean flag = channel.waitForConfirms();               //等待消息发布确认通知,参数为最大等待时间
            if(flag){
                System.out.println("消息"+i+"发送成功!");
            }

        }
        long endTime = System.currentTimeMillis();        //结束时间
        System.out.println("单个确认模式耗时:"+(endTime-startTime));

    }


}
