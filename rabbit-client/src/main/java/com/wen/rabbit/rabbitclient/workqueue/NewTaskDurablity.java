package com.wen.rabbit.rabbitclient.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @program: mq-final
 * @description: client
 * @author: 文天兆
 * @create: 2023-01-05 18:00
 **/
public class NewTaskDurablity {
    private final static String WORK_QUEUE_NAME = "work-queue";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            //channel.queueDeclare(WORK_QUEUE_NAME, false, false, false, null);

            //持久化
            boolean durable = true;
            channel.queueDeclare(WORK_QUEUE_NAME, durable, false, false, null);

            while (true) {

                String readLine = br.readLine();
                String message = String.join( " " , readLine);

//                channel.basicPublish( "" , WORK_QUEUE_NAME , null , message.getBytes());

                //将消息标记为持久化
                //
                channel.basicPublish("", WORK_QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());

                System.out.println( " [x] 已发送 '" + message + "'" );
            }
        }
    }
}
