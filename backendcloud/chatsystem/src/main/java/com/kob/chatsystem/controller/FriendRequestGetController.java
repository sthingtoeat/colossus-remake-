package com.kob.chatsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.chatsystem.mapper.RelationshipMapper;
import com.kob.chatsystem.mapper.UserInfoGetMapper;
import com.kob.chatsystem.pojo.Relationship;
import com.kob.chatsystem.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FriendRequestGetController {

    @Autowired
    private RelationshipMapper relationshipMapper;

    @Autowired
    private UserInfoGetMapper userInfoGetMapper;

    @RequestMapping("/chatApi/friend/request/get")
    public List<User> FriendRequestGet (@RequestParam(value = "user_id") Integer userId){
        QueryWrapper<Relationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("friend_id" , userId);
        queryWrapper.eq("status" , "待接受");
        List<Relationship> list = relationshipMapper.selectList(queryWrapper);

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        List<User> userList = new ArrayList<>();
        for(Relationship item : list){
            userQueryWrapper.eq("id" , item.getUserId() + "");
            userList.add(userInfoGetMapper.selectOne(userQueryWrapper));
            userQueryWrapper.clear();
        }
        return userList;
    }

}
