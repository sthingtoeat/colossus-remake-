package com.kob.chatsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.chatsystem.mapper.FriendSearchMapper;
import com.kob.chatsystem.mapper.RelationshipMapper;
import com.kob.chatsystem.pojo.Relationship;
import com.kob.chatsystem.pojo.User;
import com.kob.chatsystem.service.FriendSearchSerive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FriendSearchServiceImpl implements FriendSearchSerive {

    @Autowired
    private FriendSearchMapper friendSearchMapper;
    @Override
    public List<User> SearchFriendByName(String name) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",name);
        List<User> list = friendSearchMapper.selectList(queryWrapper);
        return list;
    }

}
