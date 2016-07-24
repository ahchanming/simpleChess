package com.chanming.websockets;

import com.chanming.common.RunContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chanming on 16/7/21.
 */

public class DeamonThread implements Runnable {
    private RunContext runContext;

    private static Logger logger = LoggerFactory.getLogger(DeamonThread.class);

    public DeamonThread(RunContext runContext){
        this.runContext = runContext;
    }

    @Override
    public void run() {
        while (true){
            try{
                logger.info("RoomSize is[ " + runContext.getRooms().size() + " ]");
                Thread.sleep(30000);
            }catch (Exception e){
                logger.error("");
            }
        }
    }
}
