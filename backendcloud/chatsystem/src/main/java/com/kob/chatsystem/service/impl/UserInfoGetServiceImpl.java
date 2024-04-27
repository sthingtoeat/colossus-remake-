package com.kob.chatsystem.service.impl;

import com.kob.chatsystem.mapper.UserInfoGetMapper;
import com.kob.chatsystem.service.UserInfoGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoGetServiceImpl implements UserInfoGetService {

    @Autowired
    private UserInfoGetMapper userInfoGetMapper;

    @Override
    public String GetPhotoById(Integer id) {
        return userInfoGetMapper.selectById(id).getPhoto();
    }

    @Override
    public String GetUsernameById(Integer id) {
        return userInfoGetMapper.selectById(id).getUsername();
    }
}
