package com.chanming.common;

import lombok.Data;

/**
 * Created by chanming on 16/7/13.
 */

public class Action {
    protected String code;
    protected String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
