package com.dmbjz.one;

import com.dmbjz.utils.RabbitUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.nio.charset.StandardCharsets;

/*Topic模式 生产者案例*/

/**
 * 相当于Direct模式添加了模糊匹配，可以让消费者在队列绑定时对 Routingkey 使用通配符，Routingkey 一般都是由一个或多个单词组成，多个单词之间以“.”分割
 * 如果一个消息满足同一队列的多个绑定，只会被消费一次
 * 当一个队列绑定键是#,那么这个队列将接收所有数据，就有点类似 fanout 了；如果队列绑定键当中没有#和*出现，那么该队列绑定类型就是 direct 了
 *
 *
 * # 通配符
 * 		* (star) can substitute for exactly one word.    匹配不多不少恰好1个词
 * 		# (hash) can substitute for zero or more words.  匹配一个或多个词
 * # 如:
 * 		orange.#   匹配 orange 开头的词
 *    		audit.*    只能匹配 audit 后面只有一个词语的
 *         #.audit.#  匹配中间夹杂audit的词语
 */
public class Provider {

    private static final String EXCHANGE_NAME = "TopicExchange";
    private static final String ORANGE_KEY= "*super.orange.cake";
    private static final String RABBIT_KEY = "baidu.ad.rabbit";
    private static final String LAZY_KEY = "lazy.user.admin";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        /*声明交换机
         * 参数一：交换机名称
         * 参数二：交换机类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        /*模拟不同会员等级接收到的不同消息内容*/
        for (int i = 0; i < 9; i++) {
            String message = "当前是待发送的消息，序号:"+i;
            if(i%3==0){
                channel.basicPublish(EXCHANGE_NAME,ORANGE_KEY,null,message.getBytes(StandardCharsets.UTF_8));  //发送ORANGE消息
            }
            if(i%3==1){
                channel.basicPublish(EXCHANGE_NAME,RABBIT_KEY,null,message.getBytes(StandardCharsets.UTF_8));  //发送RABBIT消息
            }
            if(i%3==2){
                channel.basicPublish(EXCHANGE_NAME,LAZY_KEY,null,message.getBytes(StandardCharsets.UTF_8));   //发送LAZY消息
            }
            System.out.println("消息发送: " + message);
        }

    }


}
