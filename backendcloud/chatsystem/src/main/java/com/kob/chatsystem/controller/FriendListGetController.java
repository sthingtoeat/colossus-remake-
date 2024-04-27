package com.kob.chatsystem.controller;

import com.kob.chatsystem.pojo.User;
import com.kob.chatsystem.service.FriendListGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FriendListGetController {

    @Autowired
    FriendListGetService friendListGetService;

    @RequestMapping("/friendList/get")
    public List<User> FriendListGet(){

        return friendListGetService.FriendListGetService();
    }
}
