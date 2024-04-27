package com.kob.chatsystem.controller;

import org.apache.ibatis.annotations.Param;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Map;

@RestController
public class MessageSendController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //聊天室的api
    @RequestMapping("/message/send")
    public String SendMessage(@RequestParam Map<String, String> map){
        rabbitTemplate.convertAndSend("chatroom","chatroom", map);
        return "信息发送成功";
    }

    //好友列表的api
    @RequestMapping("/message/sendFriend")
    public String SendMessageToFriend(@RequestParam Map<String ,String> map){
        rabbitTemplate.convertAndSend("friendList","friendList",map);
        return "好友信息发送成功";
    }
}
