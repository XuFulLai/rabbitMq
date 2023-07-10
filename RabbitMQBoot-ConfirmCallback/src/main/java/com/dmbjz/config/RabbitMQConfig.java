package com.dmbjz.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** RabbitMQ配置文件 */
@Configuration
public class RabbitMQConfig {


    @Autowired
    private RabbitProperties properties;

    public static final String CONFIRM_EXCHANGE_NAME = "confirm_exchange";         //交换机
    public static final String CONFIRM_QUEUE_NAME = "confirm_queue";               //队列
    public static final String CONFIRM_ROUTING_KEY = "key1";                       //路由Key


    /*RabbitMQ连接池，从配置文件读取参数*/
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(properties.getHost());
        cachingConnectionFactory.setPort(properties.getPort());
        cachingConnectionFactory.setUsername(properties.getUsername());
        cachingConnectionFactory.setPassword(properties.getPassword());
        cachingConnectionFactory.setVirtualHost(properties.getVirtualHost());
        cachingConnectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);      //开启连接池的publisher-confirm-type支持
        return cachingConnectionFactory;
    }


    @Bean
    public DirectExchange confirmExchange(){
        return new DirectExchange(CONFIRM_EXCHANGE_NAME);
    }

    @Bean
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    @Bean
    public Binding confirmeBind(@Qualifier("confirmExchange") DirectExchange confirmExchange,
                                @Qualifier("confirmQueue") Queue confirmQueue
                                ){
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTING_KEY);
    }



}
