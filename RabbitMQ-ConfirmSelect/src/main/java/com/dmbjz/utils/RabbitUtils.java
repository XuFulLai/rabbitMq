package com.dmbjz.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitUtils {

    private static ConnectionFactory connectionFactory;    //放到静态代码块中，在类加载时执行，只执行一次。达到工厂只创建一次，每次获取是新连接的效果

    static {
        //创建连接工厂
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("140.210.201.235");                   //设置MQ的主机地址
        connectionFactory.setPort(5672);                                //设置MQ服务端口
        connectionFactory.setVirtualHost("study");                      //设置Virtual Hosts(虚拟主机)
        connectionFactory.setUsername("heima");                         //设置MQ管理人的用户名（要在Web版先配置，保证该用户可以管理设置的虚拟主机）
        connectionFactory.setPassword("heima");
    }

    //定义提供连接对象的方法，封装
    public static Connection getConnection(){

        try {
            //创建连接对象并返回
            return connectionFactory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    //关闭通道和关闭连接工具类的方法
    public static void closeConnectionAndChanle(Channel channel, Connection connection){

        try {
            if (channel!=null) {
                channel.close();
            }
            if (connection!=null){
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
