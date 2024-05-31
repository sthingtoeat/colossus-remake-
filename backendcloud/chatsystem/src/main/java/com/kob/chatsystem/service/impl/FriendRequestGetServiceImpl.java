package com.kob.chatsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.chatsystem.mapper.RelationshipMapper;
import com.kob.chatsystem.mapper.UserInfoGetMapper;
import com.kob.chatsystem.pojo.Relationship;
import com.kob.chatsystem.pojo.User;
import com.kob.chatsystem.service.FriendRequestGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendRequestGetServiceImpl implements FriendRequestGetService {

    @Autowired
    private RelationshipMapper relationshipMapper;

    @Autowired
    private UserInfoGetMapper userInfoGetMapper;

    @Override
    public List<User> FriendRequestGet(Integer userId) {
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
