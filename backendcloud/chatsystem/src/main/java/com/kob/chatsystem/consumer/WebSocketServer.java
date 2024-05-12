package com.kob.chatsystem.consumer;

import com.alibaba.fastjson.JSON;
import com.kob.chatsystem.service.UserInfoGetService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@ServerEndpoint("/chatApi/websocket/chatroom/{id}")
public class WebSocketServer {
    private static Set<WebSocketServer> webSocketServerSet = new HashSet<>();

    private static UserInfoGetService userInfoGetService;
    private Integer userId = null;      //当前用户id
    private String userPhoto= null;     //当前用户照片
    private String username = null;     //当前用户名
    private Session session = null;     //当前用户会话
    @Autowired
    private void setUserInfoGetService(UserInfoGetService userInfoGetService) {
        WebSocketServer.userInfoGetService = userInfoGetService;
    }
    //发送信息给前端的函数
    public void sendMessage(Map<String ,String> message) {
        synchronized (this.session){        //不能改成final
            try{
                this.session.getBasicRemote().sendText(JSON.toJSONString(message));    //websocket发送信息的api
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //给当前用户发送所有在线成员的信息
    public void sendMemberInfoToCurrentUser(){
        String photo = null;
        String username = null;
        Integer size = webSocketServerSet.size();
        Map<String ,String> memberMap = new HashMap<>();                //记录当前条目的用户信息
        for(WebSocketServer item : webSocketServerSet){
            //每个链接的用户头像
            photo = item.userPhoto;
            //每个链接的用户名称
            username = item.username;
            memberMap.put("member_size" , size + "");
            memberMap.put("user_photo" ,photo);
            memberMap.put("user_name",username);
            memberMap.put("is_member_info","true");
            memberMap.put("member_id",item.userId + "");
            //给当前链接的用户发（给自己发）包括了所有人的列表，即通知我自己，都有谁在线
            sendMessage(memberMap);
        }
    }
    //给其他在线用户发送当前上线的用户的信息,与上面的函数思路类似但不一样
    public void sendCurrentUserInfoToMember(){
        Integer size = webSocketServerSet.size();

        Map<String ,String> currentUserMap = new HashMap<>();                //记录当前条目的用户信息
        currentUserMap.put("member_size" , size + "");
        currentUserMap.put("user_photo" ,userPhoto);
        currentUserMap.put("user_name",username);
        currentUserMap.put("is_member_info","true");
        currentUserMap.put("member_id",userId + "");

        for(WebSocketServer item : webSocketServerSet){
            if(item.userId == userId)continue;

            item.sendMessage(currentUserMap);
        }
    }

    //发送成员列表
    public void sendMemberInfoWhenOpen(){
        //给自己发在线成员的信息
        sendCurrentUserInfoToMember();
        //给其他人发我上线的信息
        sendMemberInfoToCurrentUser();
    }

    //自己需要离线，所以向其他(除了自己以外)在线成员发送自己的用户的ID
    public void sendOfflineMemberIdWhenClose(){
        for(WebSocketServer item: webSocketServerSet){
            HashMap<String ,String> map = new HashMap<>();
            map.put("is_offline_info","true");
            map.put("offline_id" , userId + "");
            if(item.userId != userId){
                item.sendMessage(map);
            }
        }
    }

    @OnOpen
    public void onOpen(Session session ,@PathParam("id") String id) {
        // 建立连接
        this.session = session;
        webSocketServerSet.add(this);
        userId = Integer.parseInt(id);
        userPhoto = userInfoGetService.GetPhotoById(Integer.parseInt(id));
        username = userInfoGetService.GetUsernameById(Integer.parseInt(id));
        System.out.println("用户:"+ this.userId + "已连接成功,当前在线人数：" + webSocketServerSet.size());
        //给所有在线成员发送更新后的成员列表
        sendMemberInfoWhenOpen();
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        sendOfflineMemberIdWhenClose();
        webSocketServerSet.remove(this);
        System.out.println("用户:"+ userId + "已离线");
    }

    @OnMessage
    public void onMessage(String message) {
        // 从Client接收消息
        System.out.println("从前端接收到信息：" + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        //报错了也得宣布下线
        sendOfflineMemberIdWhenClose();
        webSocketServerSet.remove(this);
        //打印错误报告
        error.printStackTrace();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "chatroom"),
            exchange = @Exchange(value = "chatroom" , type = "direct"),
            key = "chatroom"
    ))
    public void receive(Map<String , String> msg , @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException, EncodeException {
        System.out.println("Ws服务器接收到：" + msg);
        channel.basicAck(deliveryTag , true);
        //从传来的msg中取出用户id(谁发送的)
        Integer userSendId = Integer.parseInt(msg.get("user_id"));
        //把用户名称和头像装进msg中,需要取出的id查询数据，不能直接等于当前链接的信息，因为可能不是本人
        msg.put("user_name" , userInfoGetService.GetUsernameById(userSendId));
        msg.put("user_photo" , userInfoGetService.GetPhotoById(userSendId));

        for(WebSocketServer item : webSocketServerSet){
            //多给自己发一条影响不大，反正在前端会被覆盖掉
            item.sendMessage(msg);
            System.out.println("聊天信息已成功发送给用户" + item.userId);
        }
    }
}
