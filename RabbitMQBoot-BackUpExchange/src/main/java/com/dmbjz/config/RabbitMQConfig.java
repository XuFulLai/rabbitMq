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

    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";
    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";
    public static final String BACKUP_EXCHANGE_NAME = "backup.exchange";
    public static final String BACKUP_QUEUE_NAME = "backup.queue";
    public static final String WARNING_QUEUE_NAME = "warning.queue";


    @Autowired
    private RabbitProperties properties;

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
        cachingConnectionFactory.setPublisherReturns(properties.isPublisherReturns());      //开启连接池的ReturnCallBack支持
        return cachingConnectionFactory;
    }

    /* ========== 创建队列 ========== */
    @Bean
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    @Bean
    public Queue backupQueue(){
        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }

    @Bean
    public Queue warnQueue(){
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }


    /* ========== 创建交换机 ========== */
    @Bean
    public DirectExchange directExchange(){
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME)
                .durable(false)
                .withArgument("alternate-exchange",BACKUP_EXCHANGE_NAME)        //设置备份交换机
                .build();
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(BACKUP_EXCHANGE_NAME,false,false,null);
    }


    /* ========== 创建绑定关系 ========== */
    @Bean
    public Binding confirmBind(@Qualifier("confirmQueue") Queue queue,
                               @Qualifier("directExchange") DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).with("key1");
    }


    @Bean
    public Binding backupBind(@Qualifier("backupQueue") Queue queue,
                               @Qualifier("fanoutExchange") FanoutExchange fanoutExchange){
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }


    @Bean
    public Binding warnBind(@Qualifier("warnQueue") Queue queue,
                              @Qualifier("fanoutExchange") FanoutExchange fanoutExchange){
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }


}
