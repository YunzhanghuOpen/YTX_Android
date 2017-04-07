package com.yuntongxun.ecdemo.ui.chatting.base.emoji;

/**
 * Created by fantasy on 16/6/23.
 */
public class BQMMEmoji {

    private int code;
    private int res;

    public BQMMEmoji(int code, int res) {
        this.code = code;
        this.res = res;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
