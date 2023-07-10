package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/*临时队列演示案例*/
public class TempQueue {

      private static String QUEUE_NBAME = "tempQueue";

      public static void main(String[] args) throws Exception {
          provider();
      }

      public static void provider() throws Exception {

          Connection connection = RabbitUtils.getConnection();
          Channel channel = connection.createChannel();

          //channel.queueDeclare(QUEUE_NBAME,false,true,true,null);  创建方法一
          String QUEUE_NBAME = channel.queueDeclare().getQueue();   //队列创建方法二

          String info = "这里是临时队列";
          channel.basicPublish("",QUEUE_NBAME,null,info.getBytes(StandardCharsets.UTF_8));
          System.out.println("消息发送完成!");

          DeliverCallback successBack = (consumerTag,message)->{
              System.out.println("获取到的消息为:"+new String(message.getBody()));
          };
          CancelCallback failedBack = (consumerTag)->{
              System.out.println("获取到的消息为:"+consumerTag);
          };

          channel.basicConsume(QUEUE_NBAME,successBack,failedBack);

      }


}
