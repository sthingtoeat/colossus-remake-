package com.kob.chatsystem.controller;

import com.kob.chatsystem.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RelationshipController {
    @Autowired
    private RelationshipService relationshipService;

    @RequestMapping("/chatApi/friend/makeFriend")
    public String MakeFriend(@RequestParam Map<String ,String> map){
        String userId = map.get("user_id");
        String friendId = map.get("friend_id");
        return relationshipService.MakeFriend(userId ,friendId);
    }

    @RequestMapping("/chatApi/friend/rejectFriend")
    public String RejectFriend(@RequestParam Map<String ,String> map){
        String userId = map.get("user_id");
        String friendId = map.get("friend_id");
        return relationshipService.RejectFriend(userId , friendId);
    }
}
