package com.kob.chatsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.chatsystem.mapper.RelationshipMapper;
import com.kob.chatsystem.mapper.UserInfoGetMapper;
import com.kob.chatsystem.pojo.Relationship;
import com.kob.chatsystem.pojo.User;
import com.kob.chatsystem.service.FriendListGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendListGetServiceImpl implements FriendListGetService {
    @Autowired
    private UserInfoGetMapper userInfoGetMapper;

    @Autowired
    private RelationshipMapper relationshipMapper;

    @Override
    public List<User> FriendListGetService(Integer userId) {
        QueryWrapper<Relationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);          //双向关系，所以查自己就行
        queryWrapper.eq("status","已是好友");
        List<Relationship> list = relationshipMapper.selectList(queryWrapper);
        List<User> infoList = new ArrayList<>();
        for(Relationship item : list){
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("id" , item.getFriendId());
            infoList.add(userInfoGetMapper.selectOne(userQueryWrapper));
        }

        return infoList;
    }
}
