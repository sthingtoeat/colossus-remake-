package com.kob.chatsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.chatsystem.mapper.RelationshipMapper;
import com.kob.chatsystem.mapper.UserInfoGetMapper;
import com.kob.chatsystem.pojo.Relationship;
import com.kob.chatsystem.pojo.User;
import com.kob.chatsystem.service.FriendRequestGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FriendRequestGetController {
    @Autowired
    FriendRequestGetService friendRequestGetService;
    @RequestMapping("/chatApi/friend/request/get")
    public List<User> FriendRequestGet (@RequestParam(value = "user_id") Integer userId){
        return friendRequestGetService.FriendRequestGet(userId);
    }

}
