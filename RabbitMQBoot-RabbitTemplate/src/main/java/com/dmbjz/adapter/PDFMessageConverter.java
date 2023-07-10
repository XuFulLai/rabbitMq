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


/*PDF消息转换器*/
public class PDFMessageConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		throw new MessageConversionException("无法将对象转换为PDF! ");
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {

		System.err.println("-----------PDF消息开始转换----------");
		byte[] body = message.getBody();
		String fileName = UUID.randomUUID().toString();
    	String path = "Z:/rabbitmq/" + fileName + ".pdf";
		File f = new File(path);
		try {
			Files.copy(new ByteArrayInputStream(body), f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;

	}

}
