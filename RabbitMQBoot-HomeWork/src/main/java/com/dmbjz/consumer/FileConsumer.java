package com.dmbjz.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Component
@Slf4j
public class FileConsumer {


    @RabbitListener(queues = "FileQueue",messageConverter = "fileMessageConverter")
    public void picConsumer(Message message, Channel channel) throws IOException {

        MessageProperties messageProperties = message.getMessageProperties();
        String contentType = messageProperties.getContentType();
        String fileName = UUID.randomUUID().toString();
        StringBuffer filePath = new StringBuffer().append("Z:/rabbitmq/consumer/");
        switch (contentType){
            case "png":
                log.info("收到图片消息,开始保存");
                filePath.append(fileName).append(".png");
                break;

            case "pdf":
                log.info("收到PDF消息,开始保存");
                filePath.append(fileName).append(".pdf");
                break;

            default: break;
        }

        File f = new File(filePath.toString());
        try {
            Files.copy(new ByteArrayInputStream(message.getBody()), f.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* 手动消息应答 */
        Long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag,false);

    }


}
