package com.dmbjz.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/*文件消息转换器*/
@Slf4j
public class FileMessageConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		throw new MessageConversionException("无法将对象转换为文件对象！");
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {

		log.info("-----------文件消息转换器开始解析消息进行重构----------");

		String contentType = message.getMessageProperties().getContentType();

		if(contentType.equals("application/pdf")){
			message.getMessageProperties().setContentType("pdf");
		}

		if(contentType.equals("image")){
			message.getMessageProperties().setContentType("png");
		}

		log.info("-----------文件消息转换器重构消息完毕----------");

		return message;

	}


}
