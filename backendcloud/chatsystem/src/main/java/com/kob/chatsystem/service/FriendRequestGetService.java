package com.kob.chatsystem.service;

import com.kob.chatsystem.pojo.User;

import java.util.List;

public interface FriendRequestGetService {
    List<User> FriendRequestGet(Integer userId);

}
