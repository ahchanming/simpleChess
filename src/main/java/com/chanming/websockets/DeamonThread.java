package com.chanming.websockets;

import com.chanming.common.RunContext;

/**
 * Created by chanming on 16/7/21.
 */

public class DeamonThread implements Runnable {
    private RunContext runContext;

    public DeamonThread(RunContext runContext){
        this.runContext = runContext;
    }
    @Override
    public void run() {
        while (true){
            try{
                System.out.println("RoomSize is[" + runContext.getRooms().size() + "]");
                Thread.sleep(3000);
            }catch (Exception e){

            }
        }
    }
}
