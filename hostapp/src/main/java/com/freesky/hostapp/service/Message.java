package com.freesky.hostapp.service;

import android.text.TextUtils;

/**
 * Created by letgogo on 2018/11/20.
 * 消息实体
 */

public class Message implements Cloneable {
    /** 根据业务需要拓展字段 */
    //public String id;

    /**
     * 消息关联的第三方包名
     */
    public String pkg;

    /**
     * 消息内容，消息分发方定义消息协议格式
     */
    public String msg;

    public Message(String packageName, String msgContent) {
        this.pkg = packageName;
        this.msg = msgContent;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message)) {
            return false;
        }
        Message message = (Message) obj;
        return TextUtils.equals(pkg, message.pkg)
                && TextUtils.equals(msg, message.msg);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected Message clone() {
        try {
            return (Message) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
