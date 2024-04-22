package com.kob.chatsystem.consumer;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.kob.chatsystem.pojo.Message;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@ServerEndpoint("/websocket/{id}")
public class WebSocketServer {
    private static Set<WebSocketServer> webSocketServerSet = new HashSet<>();
    private Integer userId = null;
    private Session session = null;

    @OnOpen
    public void onOpen(Session session ,@PathParam("id") String id) {
        // 建立连接
        this.session = session;
        webSocketServerSet.add(this);
        this.userId = Integer.parseInt(id);
        System.out.println("用户:"+ this.userId + "已连接成功,当前在线人数：" + webSocketServerSet.size());
    }


    @OnClose
    public void onClose() {
        // 关闭链接
        webSocketServerSet.remove(this);
        System.out.println("用户:"+ userId + "已离线");
    }

    @OnMessage
    public void onMessage(String message) {
        // 从Client接收消息

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

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "chatroom"),
            exchange = @Exchange(value = "chatroom" , type = "direct"),
            key = "chatroom"
    ))
    public void receive(Map<String , String> msg , @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException, EncodeException {
        System.out.println("Ws服务器接收到：" + msg);
        channel.basicAck(deliveryTag , true);

        Integer userId = Integer.parseInt(msg.get("user_id"));
        for(WebSocketServer item : webSocketServerSet){
            if(userId == item.userId) {
                continue;
            }
            item.sendMessage(msg);
            System.out.println("已成功发送给用户" + item.userId);
        }
    }
}
