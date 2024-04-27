package com.kob.chatsystem.service.impl;

import com.kob.chatsystem.mapper.UserInfoGetMapper;
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

    @Override
    public List<User> FriendListGetService() {
        return userInfoGetMapper.selectList(null);
    }
}
