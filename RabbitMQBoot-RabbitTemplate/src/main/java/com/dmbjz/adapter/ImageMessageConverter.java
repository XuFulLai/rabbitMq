package com.dmbjz.adapter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/*图片消息转换器*/
public class ImageMessageConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		throw new MessageConversionException("无法将对象转换为图片! ");
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {

		System.err.println("-----------图片消息转换执行----------");
		
		Object headName = message.getMessageProperties().getHeaders().get("extName");
		String extName = headName == null ? "png" : headName.toString();
		
		byte[] body = message.getBody();
		String fileName = UUID.randomUUID().toString();
    	String path = "Z:/rabbitmq/" + fileName + "." + extName;
		File f = new File(path);
		try {
			Files.copy(new ByteArrayInputStream(body), f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;

	}


}
