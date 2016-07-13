package com.chanming.common;

import lombok.Data;

/**
 * Created by chanming on 16/7/13.
 */

@Data
public class StartAction extends Action {
    public StartAction(){
        this.code = "Start";
    }
}
