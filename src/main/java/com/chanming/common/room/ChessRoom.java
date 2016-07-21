package com.chanming.common.room;

import com.chanming.common.ChessAction;
import com.chanming.common.Result;
import com.chanming.common.StartAction;
import com.google.gson.Gson;

import javax.websocket.Session;
import java.util.Date;

/**
 * Created by chanming on 16/7/14.
 */

public class ChessRoom extends Room {

    public ChessRoom(String roomId, int totalNumber){
        this.roomId = roomId;
        this.totalNumber = totalNumber;
        this.createTime = new Date();
    }

    @Override
    public void fullEvent(){
        System.out.println("Room[" + roomId + "] is full, Send Ready Message");
        int tmp = 0;
        for (Session session : sessions){
            StartAction startAction = new StartAction();
            if (tmp == 0){
                startAction.setDetail("Black");
            }else{
                startAction.setDetail("White");
            }
            try {
                Result result = new Result();
                result.setSuccess(true);
                result.setModel(startAction);
                session.getBasicRemote().sendText(new Gson().toJson(result));
                System.out.println("Send OK");
            }catch (Exception e){
                System.out.println("SendText Error" + e.getMessage());
            }
            tmp++;
        }
    }
}
