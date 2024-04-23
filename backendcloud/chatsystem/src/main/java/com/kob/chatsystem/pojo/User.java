package com.kob.chatsystem.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {     //此处的User在通过MP查找数据库中的user表
    private Integer id;
    private String photo;
    private String username;
}
