package com.dmbjz.service;

import com.dmbjz.config.AbstractMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;


/* 自定义消息监听器的消息处理方法，一般为具体的业务处理逻辑 */
@Slf4j
@Service
public class OrderMessageService extends AbstractMessageListener {

    @Override
    public void receviceMessage(Message message) {
        log.info("OrderMessageService获取到消息:{}", new String(message.getBody()));
    }

}
