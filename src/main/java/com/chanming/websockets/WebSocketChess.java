package com.chanming.websockets;

import com.chanming.common.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chanming on 16/7/13.
 * 12
 */

@ServerEndpoint("/chess")
public class WebSocketChess {

    private static Logger logger = LoggerFactory.getLogger(WebSocketChess.class);

    private static Integer total = 0;

    private static ConcurrentHashMap<String, Room> roomMap = new ConcurrentHashMap<String, Room>();

    static {
        RunContext context = new RunContext(roomMap);
        DeamonThread deamonThread = new DeamonThread(context);
        Thread dThread = new Thread(deamonThread);
        logger.info("Deamon Thread Created!");
        dThread.start();
    }

    @OnMessage
    public void onMessage(String message, Session session)
            throws IOException, InterruptedException {
        Set<Session> session_list =null;
        session_list =session.getOpenSessions();
        List<String> roomList = session.getRequestParameterMap().get("roomId");
        String roomId = roomList.get(0);
        if (message.startsWith("connect")){
            doConnect(session, message);
        }else if (message.startsWith("chess")){
            String content = message.substring(5);
            ChessAction chessAction = new Gson().fromJson(content, ChessAction.class);
            chessAction.setCode("Chess");
            Room room = roomMap.get(roomId);
            if (room.vaildAction(chessAction)){
                Result result = new Result();
                result.setSuccess(true);
                result.setModel(chessAction);
                room.broadcast(new Gson().toJson(result));
            }
        }
    }

    /**
     * 处理下棋逻辑
     * @param session
     * @param message
     * @throws IOException
     * @throws InterruptedException
     */
    public void doChess(Session session, String message) throws  IOException, InterruptedException{

    }

    public void doReady(Session session, String message) throws  IOException, InterruptedException{

    }
    /**
     * 处理CONNECT请求
     * @param session
     * @param message
     * @throws IOException
     * @throws InterruptedException
     */
    public void doConnect(Session session, String message) throws IOException, InterruptedException{
        List<String> roomList = session.getRequestParameterMap().get("roomId");
        String roomId = roomList.get(0);
        logger.info("A new Client Connect and the roomid is [" + roomId + "]");
        if (roomMap.containsKey(roomId)){
            Room room = roomMap.get(roomId);
            if (room.enterRoom(session)){
                session.getUserProperties().put("roomId", roomId);
            }else{
                Result result = new Result();
                result.setSuccess(false);
                result.setErrMsg("进入房间失败");
                session.getBasicRemote().sendText(new Gson().toJson(result));
            }
        }else{
            Room room = new ChessRoom(roomId, 2);
            room.enterRoom(session);
            roomMap.put(roomId, room);
            session.getUserProperties().put("roomId", roomId);
        }
    }


    @OnOpen
    public void onOpen () {
        System.out.println("Client connected");
    }

    @OnClose
    public void onClose (Session session) {
        String roomId = (String)session.getUserProperties().get("roomId");
        if (roomId != null){
            Room room = roomMap.get(roomId);
            if (room != null){
                room.leaveRoom(session);
                if (room.getNowNumber() <= 0){
                    roomMap.remove(roomId);
                }
            }
        }
        System.out.println("Connection closed");
    }
}
