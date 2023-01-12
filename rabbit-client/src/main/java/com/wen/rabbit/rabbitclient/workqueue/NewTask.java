package com.wen.rabbit.rabbitclient.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @program: mq-final
 * @description: client
 * @author: 文天兆
 * @create: 2023-01-05 18:00
 **/
public class NewTask {
    private final static String WORK_QUEUE_NAME = "work-hello";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(WORK_QUEUE_NAME, true, false, false, null);

            while (true) {

                String readLine = br.readLine();
                String message = String.join( " " , readLine);

                //参数一：交换机，空字符串表示默认或无名交换
                //参数一：队列
                //参数一：消息持久化标记
                //参数一：消息内容


                channel.basicPublish( "" , WORK_QUEUE_NAME , null , message.getBytes());
                System.out.println( " [x] 已发送 '" + message + "'" );
            }
        }
    }
}
