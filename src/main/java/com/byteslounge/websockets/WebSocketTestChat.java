package com.byteslounge.websockets;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ServerEndpoint("/chat")
public class WebSocketTestChat {
    Set<Session> session_list =null;

	@OnMessage
    public void onMessage(String message, Session session) 
    	throws IOException, InterruptedException {

        session_list =session.getOpenSessions();
        for(Session s:session_list){
            s.getBasicRemote().sendText("当前总人数["+session_list.size()+"]---"+session.getId()+"说:"+message);
        }

    }
	
	@OnOpen
    public void onOpen () {
        System.out.println("Client connected");

    }

    @OnClose
    public void onClose () {
    	System.out.println("Connection closed");

    }

}
