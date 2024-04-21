package com.kob.chatsystem.consumer;

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
        userId = Integer.parseInt(id);
        System.out.println("用户:"+ id + "已连接成功,当前在线人数：" + webSocketServerSet.size());
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

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "chatroom"),
            exchange = @Exchange(value = "chatroom" , type = "fanout")
    ))
    public void receive(String msg , @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
//        session.getBasicRemote().sendText(msg);
        System.out.println("Ws服务器接收到：" + msg);
        channel.basicAck(deliveryTag , true);
    }
}
