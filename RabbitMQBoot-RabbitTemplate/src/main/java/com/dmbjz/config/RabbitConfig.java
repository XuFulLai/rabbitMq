package com.dmbjz.config;

import com.dmbjz.adapter.ImageMessageConverter;
import com.dmbjz.adapter.MessageDelegate;
import com.dmbjz.adapter.PDFMessageConverter;
import com.dmbjz.adapter.TextMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;


/* RabbitMQ配置文件 */
@Configuration
public class RabbitConfig {


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

    /* RabbitTemplate配置 */
    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory cachingConnectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory()); //让RabbitTemplate使用连接池
        return rabbitTemplate;
    }


    /*创建交换机*/
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("TemplateDirectEx",false,false);
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("TemplateFanoutEx",false,false);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("TemplateTopicEx",false,false);
    }


    /*创建队列*/
    @Bean
    public Queue directQueue1(){
        return new Queue("directQueue1",true);
    }

    @Bean
    public Queue directQueue2(){
        return new Queue("directQueue2",true);
    }

    @Bean
    public Queue topicQueue1(){
        return QueueBuilder.durable("topicQueue1").build();
    }

    @Bean
    public Queue topicQueue2(){
        return QueueBuilder.durable("topicQueue2").build();
    }


    /*创建绑定关系方法一*/
    @Bean
    public Binding directBind1(){
        return new Binding("directQueue1", Binding.DestinationType.QUEUE,
                "TemplateDirectEx","WeiXin",null);
    }

    @Bean
    public Binding directBind2(){
        return BindingBuilder.bind(new Queue("directQueue2",false))
                .to(new DirectExchange("TemplateDirectEx"))
                .with("WeiXin");
    }

    /*创建绑定关系方法二
    *   将Bean方法名称作为参数代入*/
    @Bean
    public Binding topicBind1(@Qualifier("topicQueue1") Queue queue,
                              @Qualifier("topicExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("user.#");
    }

    @Bean
    public Binding topicBind2(@Qualifier("topicQueue2") Queue queue,
                              @Qualifier("topicExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("vip.*");
    }


    /* 消息容器SimpleMessageListenerContainer 配置*/
    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(CachingConnectionFactory cachingConnectionFactory){

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(cachingConnectionFactory);        //设置连接池
        container.setQueues(topicQueue1(),topicQueue2(),directQueue1(),directQueue2());        //设置队列
        container.setConcurrentConsumers(1);                    //消费者数量
        container.setMaxConcurrentConsumers(10);                //最大消费者
        container.setDefaultRequeueRejected(false);             //是否设置重回队列，一般都为false，相当于 channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);     //消息应答方式,自动/手动/拒绝
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString();
            }
        });     //消费端的标签策略，每个消费端都有独立的标签，可在控制台的 channel > consumer 中查看 对应tag


        /*  消息监听器方法一 实际用消息适配器
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                System.out.println("消费者的消息"+new String(message.getBody()  ));
            }
        });  */


        /*消息监听器方法二 使用消息适配器 方案一，通用适配模式
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumerMessage");    //自定义消息处理方法名称
        adapter.setMessageConverter(new MyMessageConverter());  //添加消息转换器
        container.setMessageListener(adapter);
        */


        /*消息监听器方法二 使用消息适配器 方案二，指定不同的队列使用不同的监听方法
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setMessageConverter(new MyMessageConverter());      //添加消息转换器
        adapter.setDefaultListenerMethod("consumerMessage");        //消息适配器默认监听方法名称
        Map<String,String> queueOrTagToMethodName = new HashMap<>();
        queueOrTagToMethodName.put("directQueue1","method1");
        queueOrTagToMethodName.put("directQueue2","method2");
        adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);  //队列标识与方法名称组成的集合
        container.setMessageListener(adapter);
        */


        /*使用默认的JSON格式转换器，消息需要使用Map进行接收
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumerMessage");        //消息适配器默认监听方法名称
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        adapter.setMessageConverter(jsonMessageConverter);
        container.setMessageListener(adapter);
        */


        /*使用默认的JSON格式转换器，消息转换为具体的Java对象，需要使用对象进行接收
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumerMessage");

        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        javaTypeMapper.addTrustedPackages("*");             //允许使用所有包进行转换，默认会使用 java核心类进行转换
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

        adapter.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter);
        */


        /*使用默认的JSON格式转换器，消息转换为具体的Java对象，需要使用对象进行接收,支持多映射
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumerMessage");

        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

        Map<String,Class<?>> idClassMap = new HashMap<>();  //创建Map进行多映射指定,KEY为名称，value为类全路径
        idClassMap.put("student",com.dmbjz.entity.Student.class);
        idClassMap.put("packaged",com.dmbjz.entity.Packaged.class);
        javaTypeMapper.setIdClassMapping(idClassMap);
        javaTypeMapper.addTrustedPackages("*");             //允许使用所有包进行转换，默认会使用 java核心类进行转换

        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter);
        */


        /*多类型消息转换器，不同消息类型使用不同类型转换器进行转换*/
        MessageListenerAdapter adapter =new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("extComsumeMessage");

        ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter();      //复杂消息转换器

        TextMessageConverter textConvert = new TextMessageConverter();  //文本转换器
        converter.addDelegate("text",textConvert);
        converter.addDelegate("html/text",textConvert);
        converter.addDelegate("xml/text",textConvert);
        converter.addDelegate("text/plain",textConvert);

        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();    //JSON转换器
        converter.addDelegate("json",jsonConverter);
        converter.addDelegate("application/json",jsonConverter);

        ImageMessageConverter imageConverter = new ImageMessageConverter();     //图片转换器
        converter.addDelegate("image/png",imageConverter);
        converter.addDelegate("image",imageConverter);

        PDFMessageConverter pdfConverter = new PDFMessageConverter();           //PDF转换器
        converter.addDelegate("application/pdf",pdfConverter);

        adapter.setMessageConverter(converter);
        container.setMessageListener(adapter);

        return container;

    }


}
