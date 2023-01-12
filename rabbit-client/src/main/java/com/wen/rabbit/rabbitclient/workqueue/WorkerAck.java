package com.wen.rabbit.rabbitclient.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * @program: mq-final
 * @description: Recv
 * @author: 文天兆
 * @create: 2023-01-05 19:51
 **/
public class WorkerAck {
    private final static String WORK_QUEUE_NAME = "work-hello";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(WORK_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //告诉 RabbitMQ 一次不要给一个 worker 一个以上的消息。
        //或者，换句话说，在 worker 处理并确认前一条消息之前，不要向它发送新消息。
        //相反，它将把它分派给下一个还不忙的工人。
        channel.basicQos(1); // accept only one unack-ed message at a time (see below)

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), "UTF-8");

            System.out.println(" [x] Received '" + message + "'");
            try {
                doWork(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println(" [x] Done");

                //3，抛出异常，不返回确认消息，rabbitmq会重新发送给其他consumer
                throw new RuntimeException();

                //2，手动返回确认消息
//                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        //使用此代码，您可以确保即使在处理消息时使用 CTRL+C 终止工作程序，也不会丢失任何内容。worker 终止后不久，所有未确认的消息都会重新传递。

        //1，默认不会自动返回确认消息
        boolean autoAck = false;
        channel.basicConsume(WORK_QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {});
    }

    private static void doWork(String task) throws InterruptedException {
        for (char ch : task.toCharArray()) {
            if (ch == '.') Thread.sleep(1000);
        }
    }

}
