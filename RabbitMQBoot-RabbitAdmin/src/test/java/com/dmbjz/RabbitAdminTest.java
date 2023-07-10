package com.dmbjz;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/* RabbitAdmin Api测试类 */
@SpringBootTest
public class RabbitAdminTest {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    /*创建交换机案例*/
    @Test
    public void declareExchange(){

        rabbitAdmin.declareExchange(new DirectExchange("AdminDirectEx",false,false,null));
        rabbitAdmin.declareExchange(new FanoutExchange("AdminFanoutEx",false,false,null));
        rabbitAdmin.declareExchange(new TopicExchange("AdminTopicEx",false,false,null));

    }


    /* 创建队列案例 */
    @Test
    public void declareQueue(){

        rabbitAdmin.declareQueue(new Queue("directAdmin1",false,false,false));
        rabbitAdmin.declareQueue(QueueBuilder.durable("dieactAdmin2").build());

        rabbitAdmin.declareQueue(new Queue("fanoutAdmin1",false,false,false));
        rabbitAdmin.declareQueue(QueueBuilder.durable("fanoutAdmin2").build());

        rabbitAdmin.declareQueue(new Queue("topicAdmin1",false,false,false));
        rabbitAdmin.declareQueue(QueueBuilder.durable("topicAdmin2").build());

    }


    /* 创建绑定关系案例 */
    @Test
    public void declareBind(){

        /* 创建绑定关系方法1
         *   参数一：destination(队列名)
         *   参数二：基于什么的绑定（队列或交换机）
         *   参数三：交换机名称
         *   参数四：routingkey
         *   参数五：附加参数
         * */
        rabbitAdmin.declareBinding(new Binding("directAdmin1", Binding.DestinationType.QUEUE,
                "AdminDirectEx","info",null));


        /*创建绑定关系方法2
         *   使用链式方法进行绑定创建
         * */
        rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue("dieactAdmin2",false))
                                                .to(new DirectExchange("AdminDirectEx"))
                                                .with("info"));



        rabbitAdmin.declareBinding(new Binding("fanoutAdmin1", Binding.DestinationType.QUEUE,
                "AdminFanoutEx","",null));
        rabbitAdmin.declareBinding(new Binding("fanoutAdmin2", Binding.DestinationType.QUEUE,
                "AdminFanoutEx","",null));


        rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue("topicAdmin1",false))
                .to(new DirectExchange("AdminTopicEx"))
                .with("user.#"));
        rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue("topicAdmin2",false))
                .to(new DirectExchange("AdminTopicEx"))
                .with("vip.*"));

    }


    @Test
    public void otherApi(){

        /*清空队列，参数一：队列名称，参数二：是否需要等待*/
        rabbitAdmin.purgeQueue("directAdmin1",false);

    }


}
