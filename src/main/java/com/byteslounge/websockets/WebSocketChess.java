package com.byteslounge.websockets;

import com.chanming.common.Action;
import com.chanming.common.ChessAction;
import com.chanming.common.StartAction;
import com.google.gson.Gson;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;

/**
 * Created by chanming on 16/7/13.
 */

@ServerEndpoint("/chess")
public class WebSocketChess {
    @OnMessage
    public void onMessage(String message, Session session)
            throws IOException, InterruptedException {
        Set<Session> session_list =null;
        session_list =session.getOpenSessions();
        if (message.startsWith("connect")){
            System.out.println("A new Client Connect");
            System.out.println("client Number is [" + session_list.size() + "]");
            if (session_list.size() == 2){
                System.out.println("Ready Start Game");
                //first
                Session session1 = (Session)session_list.toArray()[0];
                StartAction startAction1 = new StartAction();
                startAction1.setDetail("Black");
                session1.getBasicRemote().sendText(new Gson().toJson(startAction1));

                //second
                Session session2 = (Session)session_list.toArray()[1];
                StartAction startAction2 = new StartAction();
                startAction2.setDetail("White");
                session2.getBasicRemote().sendText(new Gson().toJson(startAction2));
            }
        }else if (message.startsWith("chess")){
            String content = message.substring(5);
            ChessAction chessAction = new Gson().fromJson(content, ChessAction.class);
            chessAction.setCode("Chess");
            for (Session each : session_list){
                    each.getBasicRemote().sendText(new Gson().toJson(chessAction));
            }
            System.out.println(content);
            System.out.println(new Gson().toJson(chessAction));
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
