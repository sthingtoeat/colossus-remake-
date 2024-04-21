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

    @RequestMapping("/message/send1")
    public String SendMessage(@RequestParam Map<String, String> map){
        String userId = map.get("user_id");
        String content = map.get("user_content");
        String time = map.get("time");
        rabbitTemplate.convertAndSend("chatroom","","用户：" + userId + "在" + time+"时刻发表了：" + content);
        return "success";
    }

    @RequestMapping("/message/send_all")
    public String SendMessageAll(String msg){
        rabbitTemplate.convertAndSend("chatroom","",msg);
        return "success";
    }
}
