package com.kob.chatsystem.service.impl;

import com.kob.chatsystem.mapper.UseInfoGetMapper;
import com.kob.chatsystem.service.UserInfoGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoGetServiceImpl implements UserInfoGetService {

    @Autowired
    private UseInfoGetMapper useInfoGetMapper;

    @Override
    public String GetPhotoById(Integer id) {
        return useInfoGetMapper.selectById(id).getPhoto();
    }

    @Override
    public String GetUsernameById(Integer id) {
        return useInfoGetMapper.selectById(id).getUsername();
    }
}
