package com.kob.chatsystem.controller;

import com.kob.chatsystem.pojo.User;
import com.kob.chatsystem.service.FriendListGetService;
import com.kob.chatsystem.service.FriendSearchSerive;
import com.kob.chatsystem.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FriendSearchController {

    @Autowired
    private FriendSearchSerive friendSearchSerive;

    @RequestMapping("/chatApi/friend/search")
    public User SearchFriendByName(@RequestParam Map<String ,String> map){
        List<User> list = friendSearchSerive.SearchFriendByName(map.get("friendName"));
        if(list.size() > 1){
            System.out.println("查到了多个好友用户，停止传输");
        }
        return list.get(0);
    }
}
