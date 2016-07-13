package com.chanming.common;

import lombok.Data;

/**
 * Created by chanming on 16/7/13.
 */

@Data
public class ChessAction extends Action{
    private String color;
    private Integer x;
    private Integer y;
}
