package com.wen.rabbit.rabbitclient.router;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @program: mq-final
 * @description: EmitLogDirect
 * @author: 文天兆
 * @create: 2023-01-06 16:01
 **/
public class EmitLogDirect {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            //策略为直接交换
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String severity = getSeverity(argv);
            String message = getMessage(argv);

            //key相当于推送到当前exchange的特定队列，在接收时，需要绑定队列，exchange，key三者一起绑定，定位一个worker
            channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
        }
    }

    private static String getSeverity(String[] strings) {
        if (strings.length < 1)
            return "info";
        return strings[0];
    }

    private static String getMessage(String[] strings) {
        if (strings.length < 2)
            return "Hello World!";
        return joinStrings(strings, " ", 1);
    }

    private static String joinStrings(String[] strings, String delimiter, int startIndex) {
        int length = strings.length;
        if (length == 0)
            return "";
        if (length <= startIndex)
            return "";
        StringBuilder words = new StringBuilder(strings[startIndex]);
        for (int i = startIndex + 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }
}