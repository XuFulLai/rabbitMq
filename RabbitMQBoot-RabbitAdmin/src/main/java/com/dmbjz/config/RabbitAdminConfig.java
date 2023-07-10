package com.dmbjz.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*RabbitMQ配置文件*/
@Configuration
public class RabbitAdminConfig {

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
        return cachingConnectionFactory;
    }

    /*配置RabbitAdmin*/
    @Bean
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory cachingConnectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(cachingConnectionFactory);
        /* 低版本SpringBoot的 RabbitAdmin配置需要开启该选项 */
        // rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }


}
