package com.dmbjz.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/* RabbitMQ的交换机、队列配置文件 */
@Configuration
public class ExchangeQueueConfig {

    public static final String X_EXCHANGE = "X";                    //普通交换机
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";        //死信交换机
    public static final String DEAD_LETTER_QUEUE = "QD";

    public static final String QUEUE_C = "QC";      //消息TTL案例普通队列

    /*创建X交换机*/
    @Bean
    public DirectExchange xExchange(){
        return new DirectExchange(X_EXCHANGE);
    }

    /*创建死信交换机*/
    @Bean
    public DirectExchange yExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    //声明队列 A ttl 为 10s 并绑定到对应的死信交换机
    @Bean("queueA")
    public Queue queueA(){
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);      //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-routing-key", "YD");                     //声明当前队列的死信路由 key
        args.put("x-message-ttl", 10000);                                //声明队列的 TTL
        return QueueBuilder.durable(QUEUE_A).withArguments(args).build();
    }


    // 声明队列 A 绑定 X 交换机
    @Bean
    public Binding queueaBindingX(@Qualifier("queueA") Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }


    //声明队列 B ttl 为 40s 并绑定到对应的死信交换机
    @Bean("queueB")
    public Queue queueB(){
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);         //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-routing-key", "YD");                        //声明当前队列的死信路由 key
        args.put("x-message-ttl", 40000);                                   //声明队列的 TTL
        return QueueBuilder.durable(QUEUE_B).withArguments(args).build();
    }

    //声明队列 B 绑定 X 交换机
    @Bean
    public Binding queuebBindingX(@Qualifier("queueB") Queue queue1B,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queue1B).to(xExchange).with("XB");
    }

    //声明死信队列 QD
    @Bean("queueD")
    public Queue queueD(){
        return new Queue(DEAD_LETTER_QUEUE);
    }

    //声明死信队列 QD 绑定关系
    @Bean
    public Binding deadLetterBindingQAD(@Qualifier("queueD") Queue queueD,
                                        @Qualifier("yExchange") DirectExchange yExchange){
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }


    //声明队列 QC
    @Bean
    public Queue queueC(){
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);      //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-routing-key", "YD");                     //声明当前队列的死信路由 key
        return QueueBuilder.durable(QUEUE_C).withArguments(args).build();
    }

    //声明队列 QC 绑定 X 交换机
    @Bean
    public Binding queuebCBindingX(@Qualifier("queueC") Queue queueC,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueC()).to(xExchange).with("XC");
    }


}
