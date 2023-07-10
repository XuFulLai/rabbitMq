package com.dmbjz.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.nio.charset.StandardCharsets;

/* 自定义消息转换器 */
public class MyMessageConverter implements MessageConverter {

    /*将 Java 对象转换为 Message 对象 */
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(object.toString().getBytes(StandardCharsets.UTF_8),messageProperties);
    }

    /* 将 Message对象转换为 Java 对象 */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        String contentType = message.getMessageProperties().getContentType();
        /*判断消息类型，这里将JSON消息转换为String格式
        *   String格式数据无法被消息适配器默认的 byte[]参数接收，需要添加String参数方法
        */
        if(null!=contentType && contentType.contains("application/json")){
            return new String(message.getBody());
        }
        return message.getBody();
    }

}
