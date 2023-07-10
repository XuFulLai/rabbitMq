package com.dmbjz.config;


import com.dmbjz.service.OrderMessageService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Autowired
    private RabbitProperties properties;

    public static final String EXCHANGE_NAME = "CustomListener-Exchange";
    public static final String QUEUE_NAME = "CustomListener-Queue";
    public static final String Routing_Key = "CustomListener-Key";

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



    /* 设置自定义监听方法 */
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory,
                                                                         OrderMessageService orderMessageService){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue());
        container.setExposeListenerChannel(true);       //是否将监听器交给 ChannelAwareMessageListener，低版本SpringBoot需要手动开启
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(orderMessageService);          //对队列使用自定义监听器方法进行处理
        return container;
    }



}
