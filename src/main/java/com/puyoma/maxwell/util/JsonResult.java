package com.puyoma.maxwell.util;

import java.io.Serializable;
/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.util
 * @date:2020/3/2
 */
public class JsonResult<T> implements Serializable {
    /**
     * @Fields serialVersionUID : (序列化)
     */
    private static final long serialVersionUID = -3566409665713820424L;
    private boolean success;
    private String message;
    private T data;
    public JsonResult(){}

    public JsonResult(T data) {
        this.success = Boolean.TRUE;
        this.data = data;
    }

    public JsonResult(Throwable e) {
        success = Boolean.FALSE;
        message = e.getMessage();
    }

    public static JsonResult success(String msg){
        JsonResult jr = new JsonResult();
        jr.setSuccess(Boolean.TRUE);
        jr.setMessage(msg);
        return jr;
    }

    public static JsonResult failure(String msg){
        JsonResult jr = new JsonResult();
        jr.setSuccess(Boolean.FALSE);
        jr.setMessage(msg);
        return jr;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}