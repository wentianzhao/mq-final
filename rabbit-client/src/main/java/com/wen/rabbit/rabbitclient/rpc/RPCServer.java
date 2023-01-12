package com.wen.rabbit.rabbitclient.rpc;

import com.rabbitmq.client.*;

public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static int fib(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //绑定消费队列
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

        channel.queuePurge(RPC_QUEUE_NAME);

        //按空闲分配，负载均衡
        channel.basicQos(1);

        System.out.println(" [x] Awaiting RPC requests");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            //绑定唯一的回调id,correlationId：用于将 RPC 响应与请求相关联。
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            String response = "";

            try {
                String message = new String(delivery.getBody(), "UTF-8");
                int n = Integer.parseInt(message);

                System.out.println(" [.] fib(" + message + ")");
                response += fib(n);
            } catch (RuntimeException e) {
                System.out.println(" [.] " + e);
            } finally {

                //replyTo：通常用于命名回调队列。
                //replyTo，它被设置为专为请求创建的匿名排他队列
                //replyProps，把回调队列地方发过去,
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));

                //手动返回消息确认
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {}));
    }
}