package com.kob.chatsystem.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MessageSendController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/message/send")
    public String SendMessage(@RequestParam Map<String, String> map){
        rabbitTemplate.convertAndSend("chatroom","chatroom", map);
        return "信息发送成功";
    }

    @RequestMapping("/message/send_all")
    public String SendMessageAll(String msg){
        rabbitTemplate.convertAndSend("chatroom","",msg);
        return "success";
    }
}
