package com.dmbjz.adapter;


import com.dmbjz.entity.Packaged;
import com.dmbjz.entity.Student;

import java.io.File;
import java.util.Map;

/* 自定义消息适配器 */
public class MessageDelegate {

    public void handleMessage(byte[] messageBody){
        System.out.println("默认方法，消息内容: "+new String(messageBody));
    }

    public void consumerMessage(byte[] messageBody){
        System.out.println("自定义名称适配器方法，消息内容: "+new String(messageBody));
    }

    /*添加String参数方法接收转换为String的消息*/
    public void consumerMessage(String messageInfo){
        System.out.println("自定义名称转换String，消息内容: "+messageInfo);
    }

    public void method1(byte[] messageBody){
        System.out.println("method1收到消息内容: "+new String(messageBody));
    }

    public void method2(byte[] messageBody){
        System.out.println("method2收到消息内容: "+new String(messageBody));
    }

    public void method1(String messageInfo){
        System.out.println("method1只接收String方法，收到消息内容: "+ messageInfo);
    }

    public void method2(String messageInfo){
        System.out.println("method2只接收String方法，收到消息内容: "+ messageInfo);
    }

    /*添加Map参数方法接收JSON格式转换器消息*/
    public void consumerMessage(Map map){
        System.out.println("JSON转换器接收方法，消息内容: "+map);
    }

    /*添加对象参数方法接收JSON格式转换器消息*/
    public void consumerMessage(Student student){
        System.out.println("JSON转换器接收Student实体类方法，Name: "+student.getName()
                +"ID："+student.getId()
                +"Content: "+student.getContent());
    }

    /*添加对象参数方法接收JSON格式转换器消息*/
    public void consumerMessage(Packaged packaged){
        System.out.println("JSON转换器接收Packaged实体类方法，Name: "+packaged.getPname()
                +"ID："+packaged.getIds()
                +"Content: "+packaged.getPdesc());
    }


    /*多消息类型转换器*/
    public void extComsumeMessage(File file){
        System.out.println("文件对象内容: "+file.getName());
    }


}
