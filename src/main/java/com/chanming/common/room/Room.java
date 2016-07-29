package com.chanming.common.room;

import com.chanming.common.Action;
import com.chanming.common.context.UserStatus;
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
    Map<Session, UserStatus> sessions;

    public boolean enterRoom(Session s){
        if (sessions == null){
            sessions = new HashMap<Session, UserStatus>();
        }
        if (nowNumber < totalNumber){
            sessions.put(s, new UserStatus(s));
            nowNumber++;
            if (nowNumber == totalNumber){
                fullEvent();
            }
            return true;
        }
        return false;
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
     * 房间满人触发的事件
     */
    public void fullEvent(){

    }

    /**
     * 所有人都准备好的事件
     */
    public void allReadyEvent(){

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
