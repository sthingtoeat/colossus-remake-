package com.kob.chatsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kob.chatsystem.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FriendSearchMapper extends BaseMapper<User> {
}
