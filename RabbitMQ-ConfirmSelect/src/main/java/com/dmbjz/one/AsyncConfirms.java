package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/*发布确认 异步确认模式*/

/**
 * @author xfl
 * 异步处理模式：
 * 生产者将消息转换为Map（key为消息标记，value为消息内容）的发送到Channel中。MQ对消息持久化后， broker会通知生产者消息是否确认，生产者只需要通过监听器确认消息确认情况即可
 * 确认模式为单条确认或者批量确认，具体选择哪种由MQ自行判断，使用该方案需要对确认回调函数进行详细设计
 */
public class AsyncConfirms {

    private static final int MESSAGE_COUNT = 1000;
    private static final String QUEUE_NAME = UUID.randomUUID().toString();

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);		//队列初始化
        channel.confirmSelect();                            //设置消息确认

        /*用于存储消息的线程安全队列
         *ConcurrentSkipListMap相比ConcurrentHashMap有着更高的并发支持，有序map容器，原理为跳表，详情查看Redis章节Zset原理
         */
        ConcurrentSkipListMap<Long,Object> infoMap = new ConcurrentSkipListMap();


        /*消息确认成功的回调函数
         * 参数一:  消息的标记
         * 参数二:  是否批量确认
         */
        ConfirmCallback successBack = (deliveryTag,multiple)->{

            /*删除掉已确认的消息,剩余的就是未确认的消息，批量使用区间删除*/
            if(multiple){
                ConcurrentNavigableMap<Long, Object> confirmed =
                        infoMap.headMap(deliveryTag);  //从key=null到截止key为指定key的集合（指定key开区间）
                confirmed.clear();
            }else{
                infoMap.remove(deliveryTag);
            }
            System.out.println("确认的消息: "+deliveryTag);

        };


        /*消息确认失败的回调函数
         * 参数一:  消息的标记
         * 参数二:  是否批量确认
         */
        ConfirmCallback failedBack = (deliveryTag, multiple) -> {
            String info = String.valueOf(infoMap.get(deliveryTag));
            System.out.println("未确认的消息标记: "+deliveryTag+"  ,消息是:"+info);
        };

        /*设置异步确认模式的消息确认监听器
         * 参数一: 消息确认成功的回调函数
         * 参数二: 消息确认失败的回调函数
         */
        channel.addConfirmListener(successBack,failedBack);

        long startTime = System.currentTimeMillis();        //开始时间

        /*发送消息*/
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "当前是第"+i+"条消息";
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes(StandardCharsets.UTF_8));

            /*记录所有要发送的消息,key为下一次发布信息的序号*/
            infoMap.putIfAbsent(channel.getNextPublishSeqNo(), message);
        }

        long endTime = System.currentTimeMillis();        //结束时间
        System.out.println("异步确认模式耗时:"+(endTime-startTime));

    }


}
