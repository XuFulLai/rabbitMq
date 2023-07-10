package com.dmbjz;

import com.dmbjz.entity.Packaged;
import com.dmbjz.entity.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@SpringBootTest
class RabbitMqBootRabbitTemplateApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*RabbitTemplate的API案例*/
    @Test
    public void testTemplate(){

      /*创建消息，可以指定消息具体参数*/
      MessageProperties messageProperties = new MessageProperties();
      messageProperties.getHeaders().put("desc","请求头desc参数信息描述");
      messageProperties.getHeaders().put("type","请求头type参数信息描述");
      messageProperties.setContentType("application/json");       //发送格式
      messageProperties.setContentEncoding("UTF-8");              //UTF-8格式化

      /*封装消息
      * 参数一：消息内容
      * 参数二：消息配置
      */
      Message message = new Message("这是RabbitTemplate消息".getBytes(StandardCharsets.UTF_8),messageProperties);

      /* MessagePostProcessor：发送消息前的消息拦截器
       *    可以对消息参数进行修改，例如设置优先级、请求头等
       */
      rabbitTemplate.convertAndSend("TemplateDirectEx", "WeiXin", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
              System.out.println("拦截需要发送的消息并进行二次设置");
              message.getMessageProperties().getHeaders().put("desc","请求头desc参数信息修改");
              message.getMessageProperties().getHeaders().put("attr","请求头额外新加attr参数");
              return message;
            }
      });


      /*创建消息，使用链式调用*/
      Message message2 = MessageBuilder.withBody("这是Template消息2".getBytes(StandardCharsets.UTF_8))
              .setContentType(MessageProperties.CONTENT_TYPE_JSON)
              .setMessageId("消息ID:"+ UUID.randomUUID())
              .setContentEncoding("UTF-8")
              .setHeader("desc","额外修改的信息描述")
              .setHeader("info","请求头参数2")
              .build();

      rabbitTemplate.convertAndSend("TemplateTopicEx", "user.student", message2);


      /*最简单的调用方式*/
      //rabbitTemplate.convertAndSend("TemplateTopicEx", "vip.student", "我是最简单的消息!");
      rabbitTemplate.send("TemplateTopicEx", "user.teacher.aa", message2);


    }


    /*发送消息使用JSON格式转换器*/
    @Test
    public void testSendJsonMessage() throws JsonProcessingException {

        Student student = new Student().setId("001").setName("小明").setContent("一年级一班学生");
        /*使用ObjectMapper将消息转换为JSON数据，换成FastJSON也可以*/
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(student);
        System.out.println("需要发送的消息内容:" + json);

        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message message = new Message(json.getBytes(StandardCharsets.UTF_8),properties);
        rabbitTemplate.convertAndSend("TemplateTopicEx","vip.man",message);

    }


    /*发送消息使用JSON格式转换器 转换为 Java实体类*/
    @Test
    public void testSendJavaMessage() throws JsonProcessingException {

        Student student = new Student().setId("001").setName("小明").setContent("一年级一班学生");
        /*使用ObjectMapper将消息转换为JSON数据，换成FastJSON也可以*/
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(student);
        System.out.println("需要发送的消息内容:" + json);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.getHeaders().put("__TypeId__","com.dmbjz.entity.Student");  //key为固定值，value为需要转换对象的全路径

        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("TemplateTopicEx","vip.man",message);

    }


    /*发送消息使用JSON格式转换器 转换为 Java实体类 多映射*/
    @Test
    public void testSendJavaMessage2() throws JsonProcessingException {

        /*实体类一发送消息*/
        Student student = new Student().setId("001").setName("小明").setContent("一年级一班学生");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(student);
        System.out.println("需要发送的Student消息内容:" + json);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.getHeaders().put("__TypeId__","student");  //key为固定值，value为多映射配置的实体类名称

        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("TemplateTopicEx","vip.man",message);

        /*实体类二发送消息*/
        Packaged packaged = new Packaged().setIds("002").setPname("dmbjz").setPdesc("打包内容");
        String json2 = mapper.writeValueAsString(packaged);
        System.out.println("需要发送的Package消息内容:" + json2);
        messageProperties.getHeaders().put("__TypeId__","packaged");  //key为固定值，value为 idClassMap 的 Key
        Message message2 = new Message(json2.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("TemplateTopicEx","user.man",message2);

    }


    /*发送消息使用多类型消息转换器*/
    @Test
    public void testSendExtConverterMessage() throws Exception {

        /*发送图片文件*/
        byte[] body = Files.readAllBytes(Paths.get("E:/图片/头像", "ludashi.png"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("image/png");
        messageProperties.getHeaders().put("extName", "png");
        Message message = new Message(body, messageProperties);
        rabbitTemplate.convertAndSend("TemplateDirectEx", "WeiXin", message);


        /*发送PDF文件*/
        byte[] body2 = Files.readAllBytes(Paths.get("E:/", "rabbitmq.pdf"));
        MessageProperties messageProperties2 = new MessageProperties();
        messageProperties2.setContentType("application/pdf");
        Message message2 = new Message(body2, messageProperties2);
        rabbitTemplate.convertAndSend("TemplateDirectEx", "WeiXin", message2);

    }


}
