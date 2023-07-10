package com.dmbjz.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/* 延时队列插件案例 RabbitMQ配置类 */
@Configuration
public class DelayedQueueConfig {


    private static final String delayed_queue_name = "delayed.queue";
    private static final String delayed_exchange_name = "delayed.exchange";
    private static final String delayed_routingkey = "delayed.routingkey";


    /*创建延时插件的交换机，需要使用自定义方法进行创建
    *   插件版非死信队列，不需要路由到不同的交换机进行指定过期时间，所以固定为 direct 类型交换机
    * */
    @Bean
    public CustomExchange delayedExchange(){

        Map<String,Object> map = new HashMap<>(1);
        map.put("x-delayed-type","direct");       //延迟队列类型，固定值

        return new CustomExchange(delayed_exchange_name,"x-delayed-message",
                true,false,map);

    }

    /*队列*/
    @Bean
    public Queue delayQueue(){
        return QueueBuilder.durable(delayed_queue_name).build();
    }

    /*绑定，自定义交换机绑定多一个 noargs方法 */
    @Bean
    public Binding delayBing(@Qualifier("delayQueue") Queue delayQueue,
                             @Qualifier("delayedExchange") CustomExchange delayedExchange){
        return BindingBuilder.bind(delayQueue).to(delayedExchange)
                .with(delayed_routingkey)
                .noargs();
    }


}
