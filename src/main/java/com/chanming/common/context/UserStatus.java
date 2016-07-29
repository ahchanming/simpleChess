package com.chanming.common.context;

import lombok.Getter;
import lombok.Setter;

import javax.websocket.Session;

/**
 * Created by chanming on 16/7/27.
 */

public class UserStatus {
    /**
     * ÓÃ»§Session
     */
    Session session;
    public UserStatus(Session session){
        this.session = session;
        this.gameStatus = GAME_STATUS.WAITING;
    }

    private @Getter @Setter Integer gameStatus;
    public interface GAME_STATUS {int WAITING = 0; int READY = 1; int RUNNING = 2;}

    public boolean isReady(){
        return gameStatus != null && gameStatus == GAME_STATUS.READY;
    }
}
