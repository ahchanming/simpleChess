package com.chanming.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by chanming on 16/7/20.
 */

public class Result {
    /**
     * 错误信息
     */
    private @Getter @Setter String errMsg;
    /**
     * 返回实体
     */
    private @Getter @Setter Object model;
    /**
     * 是否成功
     */
    private @Getter @Setter Boolean success;
}
