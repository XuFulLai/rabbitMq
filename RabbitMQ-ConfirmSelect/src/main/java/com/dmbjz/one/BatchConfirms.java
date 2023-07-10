package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


/**
 * @author xufulai
 * 批量确认模式：
 * 使多个消息一起进行确认，相比单确认可以缩短时间。缺点是当某批确认发生故障导致发布出现问题时，将无法得知具体是哪个消息出现问题
 */ /*消息应答 批量确认案例*/

public class BatchConfirms {

    private static final int MESSAGE_COUNT = 1000;
    private static final String QUEUE_NAME = UUID.randomUUID().toString();

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);		//队列初始化
        channel.confirmSelect();                            //设置消息确认
        long startTime = System.currentTimeMillis();        //开始时间

        for (int i = 1; i <= MESSAGE_COUNT; ++i) {

            String message = "当前是" + i + "条消息";
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes(StandardCharsets.UTF_8));     //消息发送

            if(i%100==0){
                boolean flag = channel.waitForConfirms();               //每100条消息进行一次消息确认
                if(flag){
                    System.out.println("消息"+i+"发送成功!");
                }
            }

        }
        long endTime = System.currentTimeMillis();        //结束时间
        System.out.println("批量确认模式耗时:"+(endTime-startTime));

    }


}
