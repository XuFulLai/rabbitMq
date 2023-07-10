package com.dmbjz.config;


import com.dmbjz.converter.FileMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
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

    private static final String EXCHANGE_NAME = "TeacherExchange";
    private static final String QUEUE_NAME = "TeacherQueue";
    private static final String ROUTING_KEY = "Teacher.#";

    private static final String FILE_QUEUE_NAME = "FileQueue";
    private static final String FILE_ROUTING_KEY = "File.#";

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
        cachingConnectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);      //开启连接池的publisher-confirm-type支持
        return cachingConnectionFactory;
    }

    /* RabbitListener使用连接池，使用连接池时 spring.rabbitmq.listener.simple 配置不生效 */
    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);         //关闭自动ACK
        factory.setConnectionFactory(connectionFactory);            //使用连接池
        factory.setPrefetchCount(1);                                //设置QOS
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public TopicExchange teacherExchange(){
        return new TopicExchange(EXCHANGE_NAME,true,false);
    }

    @Bean
    public Queue teacherQueue(){
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding teacherBind(@Qualifier("teacherExchange") TopicExchange exchange,
                               @Qualifier("teacherQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }


    @Bean
    public Queue fileQueue(){
        return QueueBuilder.durable(FILE_QUEUE_NAME).build();
    }

    @Bean
    public Binding fileBind(@Qualifier("teacherExchange") TopicExchange exchange,
                            @Qualifier("fileQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(FILE_ROUTING_KEY);
    }


    /* 注入自定义消息转换器 */
    @Bean
    public FileMessageConverter fileMessageConverter(){
        return new FileMessageConverter();
    }


}
