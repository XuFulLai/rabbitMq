package com.dmbjz.config;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.io.IOException;


/*  自定义消息监听，可以编写公共消息应答方法 */
@Slf4j
public abstract class AbstractMessageListener implements ChannelAwareMessageListener {


    public abstract void receviceMessage(Message message);

    /* 获取到的队列消息执行抽象方法，抽象方法实际落地就是 OrderMessageService 的 receviceMessage */
    @Override
    public void onMessage(Message message, Channel channel) throws IOException, InterruptedException {

        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        log.info("收到消息{}: ", message);

        try{
            receviceMessage(message);
            channel.basicAck(deliveryTag , false);      //同意应答
        }catch (Exception e){
            log.error(e.getMessage(), e);
            /* 这里可以根据消息具体的参数判断是否应该拒绝重回队列 */
            if (message.getBody().length>100){
                channel.basicReject(deliveryTag, false);
            } else {
                channel.basicNack(deliveryTag, false, true);
            }
        }
    }


}
