package com.dmbjz.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/* RabbitMQ配置文件 */
@Configuration
public class RabbitMQConfig {

    @Autowired
    private RabbitProperties properties;

    public static final String EXCHANGE_NAME = "ReturnCallBack-Exchange";
    public static final String QUEUE_NAME = "ReturnCallBack-StudentA";
    public static final String Routing_Key = "ReturnCallBack-Key";


    /*RabbitMQ连接池，从配置文件读取参数*/
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(properties.getHost());
        cachingConnectionFactory.setPort(properties.getPort());
        cachingConnectionFactory.setUsername(properties.getUsername());
        cachingConnectionFactory.setPassword(properties.getPassword());
        cachingConnectionFactory.setVirtualHost(properties.getVirtualHost());
        cachingConnectionFactory.setPublisherReturns(properties.isPublisherReturns());      //开启连接池的ReturnCallBack支持
        return cachingConnectionFactory;
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(EXCHANGE_NAME,false,false,null);
    }


    @Bean
    public Queue queue(){
        return QueueBuilder.durable(QUEUE_NAME).build();
    }


    @Bean
    public Binding binding(@Qualifier("queue") Queue queue,
                           @Qualifier("directExchange") DirectExchange directExchange
                           ){
        return BindingBuilder.bind(queue).to(directExchange).with(Routing_Key);
    }



}
