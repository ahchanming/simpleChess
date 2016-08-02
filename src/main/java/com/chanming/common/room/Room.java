package com.chanming.common.room;

import com.chanming.common.Action;
import com.chanming.common.context.UserContext;
import lombok.Getter;
import lombok.Setter;

import javax.websocket.Session;
import java.util.*;

/**
 * Created by chanming on 16/7/14.
 */

public abstract class Room {
    /**
     * 房间ID
     */
    protected @Getter @Setter String roomId;

    /**
     * 房间容纳总数
     */
    protected @Getter @Setter int totalNumber;

    /**
     * 现有人数

     */
    protected @Getter @Setter int nowNumber;

    /**
     * 创建时间
     */
    protected @Getter @Setter Date createTime;

    /**
     * 在游戏中的人员
     */
    protected @Getter @Setter
    Map<Session, UserContext> sessions = new HashMap<Session, UserContext>();

    public boolean enterRoom(Session s){
        if (nowNumber < totalNumber){
            sessions.put(s, new UserContext(s));
            nowNumber++;
            return true;
        }
        return false;
    }

    public void doReady(Session s){
        UserContext userContext = sessions.get(s);
        userContext.setGameStatus(UserContext.GAME_STATUS.READY);
        if (checkAllReady()){
            startGame();
        }
    }

    public void doOver(Session s){
    }

    public void gameOver(Session s){
        for (Map.Entry<Session, UserContext> entry : sessions.entrySet()){
            entry.getValue().setGameStatus(UserContext.GAME_STATUS.PENDING);
        }
    }

    public boolean leaveRoom(Session s){
        sessions.remove(s);
        nowNumber--;
        return true;
    }

    public void broadcast(String buffer){
        for (Session session : sessions.keySet()){
            try{
                session.getBasicRemote().sendText(buffer);
            }catch (Exception e){
                System.out.println("Error\n");
            }
        }
    }

    /**
     * 游戏开始事件
     */
    public void startGame(){
        for (Map.Entry<Session, UserContext> each : sessions.entrySet()){
            each.getValue().setGameStatus(UserContext.GAME_STATUS.RUNNING);
        }
    }

    /**
     * 检查所有的选手是否已经准备完成
     * @return
     */
    public boolean checkAllReady(){
        if (nowNumber < totalNumber){
            return false;
        }
        for (Map.Entry<Session, UserContext> each : sessions.entrySet()){
            if (!each.getValue().isReady()){
                return false;
            }
        }
        return true;
    }

    public void fullEvent(){

    }

    /**
     * 判断一个ACTION是否合法
     * @param action
     * @return
     */
    public boolean vaildAction(Action action) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        return roomId.equals(room.roomId);

    }

    @Override
    public int hashCode() {
        return roomId.hashCode();
    }


}
