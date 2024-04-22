package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yupi.springbootinit.constant.BiMqConstant;

public class RabbitMqInitDemo {
    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
        	// 设置 rabbitmq 对应的信息
       	    factory.setHost("112.124.30.198");
            factory.setPort(5672);
            factory.setVirtualHost("/wcw");
            factory.setUsername("hmall");
            factory.setPassword("123");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            String demoExchange = BiMqConstant.BI_EXCHANGE_NAME;

            channel.exchangeDeclare(demoExchange, "direct");

            // 创建队列，分配一个队列名称：小紫
            String queueName = BiMqConstant.BI_QUEUE_NAME;
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, demoExchange, BiMqConstant.BI_ROUTING_KEY);

        }catch (Exception e){

        }
    }

}
