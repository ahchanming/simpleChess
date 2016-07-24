package com.chanming.common.role;

import lombok.Getter;
import lombok.Setter;

import javax.websocket.Session;

/**
 * 玩家
 * Created by chanming on 16/7/23.
 */

public class Player extends BaseRole {
    /**
     * 用户Session
     */
    private @Getter @Setter Session session;

    /**
     * 用户的颜色
     */
    private @Getter @Setter String color;

    /**
     * 用户的准备状态
     */
    private @Getter @Setter Boolean readyStatus;

    public Player(Session session){
        this.session = session;
    }

    public Player initSession(Session session){
        this.session = session;
        return this;
    }

    public Player initColor(String color){
        this.color = color;
        return this;
    }

    public Player initReadyStatus(boolean readyStatus){
        this.readyStatus = readyStatus;
        return this;
    }

}
