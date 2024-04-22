//package com.kob.chatsystem.controller;
//
//import com.rabbitmq.client.Channel;
//import org.springframework.amqp.rabbit.annotation.Exchange;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.QueueBinding;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//
//@RestController
//public class MessageReceiveController3 {
//
//    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "queue3"), exchange = @Exchange(value = "chatroom")))
//    public void consumer(String msg , @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
//        System.out.println("消费者3成功接收到消息:" + msg);
//        channel.basicAck(deliveryTag,true);
//    }
//
//
//}
