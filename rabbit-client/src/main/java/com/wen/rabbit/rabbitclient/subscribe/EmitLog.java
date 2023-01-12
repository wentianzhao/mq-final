package com.wen.rabbit.rabbitclient.subscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @program: mq-final
 * @description: EmitLog
 * @author: 文天兆
 * @create: 2023-01-06 14:03
 **/
public class EmitLog {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {

            //定义交换机名称，和交换类型
            //几种交换类型可用：direct、topic、headers 和fanout
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            /*while (true) {

                String readLine = br.readLine();
                String message = readLine.length() < 1 ? "info: Hello World!" : String.join(" ", readLine);

                //定义交换机名称，未定义队列名称，默认队列
                //未定义持久化信息
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
            }*/

            for (int i = 0; i < 1000000; i++) {
                String message = String.join("info: Hello World-", String.valueOf(i));

                //定义交换机名称
                //未定义队列名称，默认队列
                //未定义持久化信息
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }
}