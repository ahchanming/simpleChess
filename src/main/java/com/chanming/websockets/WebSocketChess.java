package com.chanming.websockets;

import com.chanming.common.Action;
import com.chanming.common.ChessAction;
import com.chanming.common.StartAction;
import com.chanming.common.room.ChessRoom;
import com.chanming.common.room.Room;
import com.google.gson.Gson;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chanming on 16/7/13.
 * 12
 */

@ServerEndpoint("/chess")
public class WebSocketChess {

    private static Integer total = 0;

    private static ConcurrentHashMap<String, Room> roomMap = new ConcurrentHashMap<String, Room>();

    @OnMessage
    public void onMessage(String message, Session session)
            throws IOException, InterruptedException {
        System.out.println(total);
        total += 1;
        Set<Session> session_list =null;
        session_list =session.getOpenSessions();
        List<String> roomList = session.getRequestParameterMap().get("roomId");
        String roomId = roomList.get(0);
        System.out.println("RoomId is" + roomId);
        if (message.startsWith("connect")){
            System.out.println("A new Client Connect");
            System.out.println("client Number is [" + session_list.size() + "]");
            if (roomList != null && roomList.size() > 0){
                if (roomMap.containsKey(roomId)){
                    Room room = roomMap.get(roomId);
                    if (room.enterRoom(session)){
                        session.getUserProperties().put("roomId", roomId);
                    }else{
                        session.getBasicRemote().sendText("error");
                    }
                }else{
                    Room room = new ChessRoom(roomId, 2);
                    room.enterRoom(session);
                    roomMap.put(roomId, room);
                    session.getUserProperties().put("roomId", roomId);
                }
            }else{

            }
        }else if (message.startsWith("chess")){
            String content = message.substring(5);
            ChessAction chessAction = new Gson().fromJson(content, ChessAction.class);
            chessAction.setCode("Chess");
            Room room = roomMap.get(roomId);
            room.broadcast(new Gson().toJson(chessAction));
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
