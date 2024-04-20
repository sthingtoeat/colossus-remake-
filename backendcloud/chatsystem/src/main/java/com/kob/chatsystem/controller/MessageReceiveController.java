package com.kob.chatsystem.controller;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageReceiveController {

    @RabbitListener(queues = "queue1" )
    public void consumer(String msg) {
        System.out.println("消费者1成功接收到消息:" + msg);

    }


}
