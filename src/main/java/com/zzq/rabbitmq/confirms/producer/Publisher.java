package com.zzq.rabbitmq.confirms.producer;

import com.rabbitmq.client.*;
import com.zzq.rabbitmq.util.RabbitMQConnectionUtil;
import org.junit.Test;

import java.io.IOException;

/**
 * @author zzq
 * @description
 * @date 2022/1/24 22:54
 */
public class Publisher {



    @Test
    public void publish() throws Exception {
        //1. 获取连接对象
        Connection connection = RabbitMQConnectionUtil.getConnection();

        //2. 构建Channel
        Channel channel = connection.createChannel();




        //3. 构建队列

        /**
         * queue：队列的名称（字符串）。指定队列名称，如果名称为空字符串，将创建一个随机命名的队列。
         *
         * durable：是否持久化队列（布尔值）。设置为 true 表示该队列是持久化的，服务器重启后队列仍然存在；设置为 false 表示非持久化队列，服务器重启后该队列将被删除。
         *
         * exclusive：是否独占队列（布尔值）。设置为 true 表示该队列仅供当前连接使用，其他连接无法访问，且连接关闭时队列自动删除；设置为 false 表示该队列可以被多个连接访问。
         *
         * autoDelete：是否自动删除队列（布尔值）。设置为 true 表示队列在不再使用时自动删除（当没有消费者时）；设置为 false 表示不自动删除队列。
         *
         * arguments：队列的其他属性（Map<String, Object> 类型）。可以设置额外的队列参数，例如消息过期时间、队列最大长度等。
         */
        channel.queueDeclare("confirms",true,false,false,null);



        //4. 开启confirms
        channel.confirmSelect();

        //5. 设置confirms的异步回调
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("消息成功的发送到Exchange！");
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("消息没有发送到Exchange，尝试重试，或者保存到数据库做其他补偿操作！");
            }
        });



        /**
         * exchange：交换机的名称（字符串）。指定消息要发布到的交换机。若该交换机不存在，会触发通道级别的协议异常，导致通道关闭。
         *
         * routingKey：路由键（字符串）。用于指定消息的路由路径。RabbitMQ 使用 routingKey 来决定消息应该发送到哪些队列。在不同类型的交换机中，routingKey 的作用可能有所不同。
         *
         * props：消息的其他属性（BasicProperties 类型）。用于设置消息的属性，如消息的优先级、持久性、内容类型等，还可以包含路由头信息。
         *
         * body：消息内容（byte[] 类型）。表示消息的实际内容，以字节数组的形式传递。
         */
        String message = "Hello World!";



        //6. 设置Return回调，确认消息是否路由到了Queue
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消息没有路由到指定队列，做其他的补偿措施！！");
            }
        });



        //7. 设置消息持久化
        AMQP.BasicProperties props = new AMQP.BasicProperties()
                .builder()
                .deliveryMode(2)
                .build();

        //8. 发布消息
        channel.basicPublish("","confirms",true,props,message.getBytes());
        System.out.println("消息发送成功！");
        System.in.read();


    }
}