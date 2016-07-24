package com.chanming.common.room;

import com.chanming.common.Action;
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

    private Integer chessBoard[][] = new Integer[16][16];
    private int maxSize = 16;
    private ChessAction lastChessAction;

    private void initChessBoard(){
        for (int i = 0; i < maxSize; ++i){
            for (int j = 0; j < maxSize; ++j){
                chessBoard[i][j] = 0;
            }
        }
    }

    public ChessRoom(String roomId, int totalNumber){
        this.roomId = roomId;
        this.totalNumber = totalNumber;
        this.createTime = new Date();
        initChessBoard();
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

    @Override
    public boolean vaildAction(Action action){
        if (action instanceof ChessAction){
            if (lastChessAction != null && ((ChessAction) action).getColor().equals(lastChessAction.getColor())){
                return false;
            }
            if (chessBoard[((ChessAction) action).getX()][((ChessAction) action).getY()] != 0){
                return false;
            }
            chessBoard[((ChessAction) action).getY()][((ChessAction) action).getY()] = ((ChessAction) action).getColor().equals("Black") ? -1 : 1;
            return true;
        }
        return false;
    }

}
