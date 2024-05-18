package com.kob.chatsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.chatsystem.mapper.RelationshipMapper;
import com.kob.chatsystem.pojo.Relationship;
import com.kob.chatsystem.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    @Autowired
    RelationshipMapper relationshipMapper;
    @Override
    public String MakeFriend(String userId ,String friendId) {
        //判断是否重复发送好友请求
        Date date = new Date();
        QueryWrapper<Relationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("friend_id",friendId);
        Relationship existedRelationship = relationshipMapper.selectOne(queryWrapper);//已有的关系
        //是否存在关系,且关系非"已拒绝"
        if(existedRelationship != null && !"已拒绝".equals(existedRelationship.getStatus())){
            //判断是不是已经互为好友
            if("已是好友".equals(existedRelationship.getStatus())) {
                return "你们已经是好友了！";
            }
            if(relationshipMapper.exists(queryWrapper) && "待接受".equals(existedRelationship.getStatus())){
                return "请勿重复发送好友请求！";
            }
        }
        //如果关系为已拒绝，那么再次发送请求，将只更新状态和时间
        if(existedRelationship != null && "已拒绝".equals(existedRelationship.getStatus())){
            existedRelationship.setStatus("待接受");
            existedRelationship.setUpdatetime(date);
            relationshipMapper.update(existedRelationship,queryWrapper);
            return "已再次向该用户发送请求";
        }
        //若非重复发送也非好友关系，则新增一条关系记录(向他人发送好友请求)，状态为待接受，标记为关系待接受，暂时先不插入，因为可能需要继续修改
        Relationship newRelationship = new Relationship();      //准备新增的关系
        newRelationship.setUserId(Integer.parseInt(userId));
        newRelationship.setFriendId(Integer.parseInt(friendId));
        newRelationship.setStatus("待接受");
        newRelationship.setCreatetime(date);
        //查询一下是否有他人向我发起好友请求记录
        queryWrapper.clear();
        queryWrapper.eq("user_id",friendId);
        queryWrapper.eq("friend_id" , userId);
        Relationship relationship = relationshipMapper.selectOne(queryWrapper);//他人发给自己的好友请求
        //如果查到记录
        if(relationship != null){
            //则判断是否已经为好友，已是好友的话则直接返回
            //非好友，则更新已查到的记录（即他人向我发出的好友请求），更新状态
            relationship.setStatus("已是好友");
            relationship.setUpdatetime(date);
            relationshipMapper.update(relationship,queryWrapper);
            //对新增的关系进行修改（我向他人发出的好友请求），然后插入数据库
            newRelationship.setStatus("已是好友");
            newRelationship.setUpdatetime(date);
            relationshipMapper.insert(newRelationship);
            return "好友状态已更新";
        }
        //查不到记录则插入新的好友关系以后返回
        relationshipMapper.insert(newRelationship);
        return "已成功向对方发送好友请求";
    }

    @Override
    public String RejectFriend(String userId, String friendId) {
        //拒绝好友，将不会新增新的好友关系，直接修改对面发来的好友请求的状态即可,当然也需要更新时间
        Date date = new Date();
        QueryWrapper<Relationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id" , friendId);//反着写，表示对面想要加我为好友的请求
        queryWrapper.eq("friend_id",userId);
        Relationship relationship = relationshipMapper.selectOne(queryWrapper);
        relationship.setStatus("已拒绝");
        relationship.setUpdatetime(date);
        relationshipMapper.update(relationship , queryWrapper);
        return "拒绝成功";
    }
}
