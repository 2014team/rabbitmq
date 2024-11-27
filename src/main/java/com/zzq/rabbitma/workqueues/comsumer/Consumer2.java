package com.zzq.rabbitma.workqueues.comsumer;

import com.rabbitmq.client.*;
import com.zzq.rabbitma.util.RabbitMQConnectionUtil;
import com.zzq.rabbitma.workqueues.producer.Publisher;
import org.junit.Test;

import java.io.IOException;

/**
 * @author zzq
 * @description
 * @date 2022/1/24 23:02
 */
public class Consumer2 {

    @Test
    public void consume1() throws Exception {
        //1. 获取连接对象
        Connection connection = RabbitMQConnectionUtil.getConnection();

        //2. 构建Channel
        Channel channel = connection.createChannel();

        //3. 构建队列
        channel.queueDeclare(Publisher.QUEUE_NAME,false,false,false,null);

        //3.5 设置消息的流控
        channel.basicQos(3);

        //4. 监听消息
        DefaultConsumer callback = new DefaultConsumer(channel){


            /**
             consumerTag（String 类型）：消费者标签，是 RabbitMQ 为每个消费者分配的一个唯一标识符。此标识符可以用于取消或标识某个特定的消费者。
             envelope（Envelope 类型）：消息的元数据。包含了路由信息，例如：
             envelope.getDeliveryTag()：消息的投递标签，可以用于确认消息。
             envelope.getExchange()：消息来自的交换机名称。
             envelope.getRoutingKey()：消息的路由键，用于路由消息到指定队列。
             properties（AMQP.BasicProperties 类型）：消息的属性，用于描述消息的其他属性信息。它可以包含消息的优先级、消息的持久性、内容类型、消息的标识符等。

             body（byte[] 类型）：消息的主体内容，以字节数组形式表示。通过 new String(body, "UTF-8") 可以将其转换为字符串格式，以便在控制台输出消息内容。
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


                /**
                 * 确认消息的接收和处理
                 */
                channel.basicAck(envelope.getDeliveryTag(),false);
                System.out.println("消费者1获取到消息：" + new String(body,"UTF-8"));
            }
        };
        channel.basicConsume(Publisher.QUEUE_NAME,false,callback);
        System.out.println("开始监听队列");

        System.in.read();
    }


    @Test
    public void consume2() throws Exception {
        //1. 获取连接对象
        Connection connection = RabbitMQConnectionUtil.getConnection();

        //2. 构建Channel
        Channel channel = connection.createChannel();

        //3. 构建队列
        channel.queueDeclare(Publisher.QUEUE_NAME,false,false,false,null);

        //3.5 设置消息的流控
        channel.basicQos(3);

        //4. 监听消息
        DefaultConsumer callback = new DefaultConsumer(channel){


            /**
             consumerTag（String 类型）：消费者标签，是 RabbitMQ 为每个消费者分配的一个唯一标识符。此标识符可以用于取消或标识某个特定的消费者。
             envelope（Envelope 类型）：消息的元数据。包含了路由信息，例如：
             envelope.getDeliveryTag()：消息的投递标签，可以用于确认消息。
             envelope.getExchange()：消息来自的交换机名称。
             envelope.getRoutingKey()：消息的路由键，用于路由消息到指定队列。
             properties（AMQP.BasicProperties 类型）：消息的属性，用于描述消息的其他属性信息。它可以包含消息的优先级、消息的持久性、内容类型、消息的标识符等。

             body（byte[] 类型）：消息的主体内容，以字节数组形式表示。通过 new String(body, "UTF-8") 可以将其转换为字符串格式，以便在控制台输出消息内容。
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("消费者2获取到消息：" + new String(body,"UTF-8"));

                /**
                 * 确认消息的接收和处理
                  */
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        channel.basicConsume(Publisher.QUEUE_NAME,false,callback);
        System.out.println("开始监听队列");

        System.in.read();
    }
}