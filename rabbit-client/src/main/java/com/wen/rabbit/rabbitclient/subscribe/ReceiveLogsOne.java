package com.wen.rabbit.rabbitclient.subscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * @program: mq-final
 * @description: ReceiveLogs
 * @author: 文天兆
 * @create: 2023-01-06 14:12
 **/
public class ReceiveLogsOne {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //使用相同的交换机，和交换策略
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        //当我们不向queueDeclare()提供任何参数时， 我们会创建一个具有生成名称的非持久的、独占的、自动删除的队列
        //生成默认队列
        String queueName = channel.queueDeclare().getQueue();

        //绑定：queue和excchange
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), "UTF-8");

            System.out.println(" [x] Received '" + message + "'");
        };
        //队列名
        //自动返回消息确认
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}