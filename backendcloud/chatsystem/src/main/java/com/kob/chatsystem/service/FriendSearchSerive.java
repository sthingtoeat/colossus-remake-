package com.kob.chatsystem.service;

import com.kob.chatsystem.pojo.User;

import java.util.List;

public interface FriendSearchSerive {
    List<User> SearchFriendByName(String name);

}
