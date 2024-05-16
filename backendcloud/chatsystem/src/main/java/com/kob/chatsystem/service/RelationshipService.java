package com.kob.chatsystem.service;

public interface RelationshipService {
    String MakeFriend(String userId , String friendId);

    String RejectFriend(String userId , String friendId);
}
