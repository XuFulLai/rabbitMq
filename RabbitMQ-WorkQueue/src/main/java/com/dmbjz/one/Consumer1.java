package com.dmbjz.one;


import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;

/*WorkQueue模型 消费者代码*/
public class Consumer1 {


  private static final String QUEUE_NAME = "hello";             //队列名称

  public static void main(String[] args) throws IOException {

      Connection connection = RabbitUtils.getConnection();      //创建连接

      Channel channel = connection.createChannel();             //创建信道


      /*消费者成功消费时的回调接口，这里为打印获取到的消息*/
      DeliverCallback deliverCallback = (consumerTag, message) -> {
          System.out.println("接收到的消息: "+ new String(message.getBody()) );
      };

      /*消费者取消消费的回调*/
      CancelCallback callback = consumerTag -> {
          System.out.println(consumerTag+"消息者取消消费接口回调逻辑");
      };

      System.out.println("消费者B等待接收消息......");

      /*  消费消息
       * 参数1 ： 消费队列的名称
       * 参数2 ： 消息的自动确认机制(一获得消息就通知 MQ 消息已被消费)  true打开，false关闭 (接收到消息并消费后也不通知 MQ ，常用)
       * 参数3 ： 消费者成功消费时的回调接口
       * 参数4 ： 消费者取消消费的回调
       */
      channel.basicConsume(QUEUE_NAME,true,deliverCallback,callback);       //消费消息



  }


}
