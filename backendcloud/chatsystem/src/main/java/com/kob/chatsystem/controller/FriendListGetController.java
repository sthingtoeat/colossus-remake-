package com.kob.chatsystem.controller;

import com.kob.chatsystem.pojo.User;
import com.kob.chatsystem.service.FriendListGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FriendListGetController {

    @Autowired
    FriendListGetService friendListGetService;

    @RequestMapping("/chatApi/friendList/get")
    public List<User> FriendListGet(@RequestParam(value = "user_id") Integer userId){
        return friendListGetService.FriendListGetService(userId);
    }
}
