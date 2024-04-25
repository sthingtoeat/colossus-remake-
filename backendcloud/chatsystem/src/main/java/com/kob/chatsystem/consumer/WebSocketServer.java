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
@ServerEndpoint("/websocket/chatroom/{id}")
public class WebSocketServer {
    private static Set<WebSocketServer> webSocketServerSet = new HashSet<>();

    private static UserInfoGetService userInfoGetService;
    private Integer userId = null;
    private Session session = null;
    @Autowired
    private void setUserInfoGetService(UserInfoGetService userInfoGetService) {
        WebSocketServer.userInfoGetService = userInfoGetService;
    }

    @OnOpen
    public void onOpen(Session session ,@PathParam("id") String id) {
        // 建立连接
        this.session = session;
        webSocketServerSet.add(this);
        this.userId = Integer.parseInt(id);
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
        error.printStackTrace();
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

    //发送成员列表，仅用户进入聊天室(打开链接时)时给自己发送,此处代码复用度有点差，需要进行优化
    public void sendMemberInfoWhenOpen(){
        Integer size = webSocketServerSet.size();                           //在线的人数
        String thisPhoto = userInfoGetService.GetPhotoById(userId);         //此链接的用户的头像
        String thisUsername = userInfoGetService.GetUsernameById(userId);   //此链接的用户的名称

        Map<String ,String> thisMap = new HashMap<>();                      //当前链接的用户信息，准备发给除了自己以外的其他人，每个人只需收到一条这个用户的信息
        thisMap.put("member_size" , size + "");
        thisMap.put("user_photo" ,thisPhoto);
        thisMap.put("user_name",thisUsername);
        thisMap.put("is_member_info","true");
        thisMap.put("member_id",userId + "");

        int x = 1;                                                          //临时变量，记录发送的条数
        //每循环一次发送一条成员信息，将发送n条，n=在线人数，当然只有自己能收到
        for(WebSocketServer item : webSocketServerSet){
            String photo = userInfoGetService.GetPhotoById(item.userId);    //每个链接的用户头像
            String username = userInfoGetService.GetUsernameById(item.userId);//每个链接的用户名称

            Map<String ,String> memberMap = new HashMap<>();                //记录当前条目的用户信息
            memberMap.put("member_size" , size + "");
            memberMap.put("user_photo" ,photo);
            memberMap.put("user_name",username);
            memberMap.put("is_member_info","true");
            memberMap.put("member_id",item.userId + "");
            //给当前链接的用户发（给自己发）包括了所有人的列表，即通知我自己，都有谁在线
            sendMessage(memberMap);

            System.out.println("已向成员" + this.userId + "发送第"+(x++)+"条成员列表信息");

            //如果当前链接是其他人，那么告诉他们，我上线了
            if(item.userId != userId){
                item.sendMessage(thisMap);
            }
        }
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

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "chatroom"),
            exchange = @Exchange(value = "chatroom" , type = "direct"),
            key = "chatroom"
    ))
    public void receive(Map<String , String> msg , @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException, EncodeException {
        System.out.println("Ws服务器接收到：" + msg);
        channel.basicAck(deliveryTag , true);
        //获取消息传递过来的用户id（谁发的）
        Integer user_id = Integer.parseInt(msg.get("user_id"));
        String user_name = userInfoGetService.GetUsernameById(user_id);
        String user_photo = userInfoGetService.GetPhotoById(user_id);
        //把用户名称和头像装进msg中
        msg.put("user_name" , user_name);
        msg.put("user_photo" , user_photo);

        for(WebSocketServer item : webSocketServerSet){

            item.sendMessage(msg);
            System.out.println("聊天信息已成功发送给用户" + item.userId);
            //多发一条影响不大，反正在前端会被覆盖掉，才不是因为下面的代码有会给自己发的bug呢！
//            if(!item.userId.equals(userId) ) {
//                item.sendMessage(msg);
//                System.out.println("聊天信息已成功发送给用户" + item.userId);
//            }
        }
    }
}
