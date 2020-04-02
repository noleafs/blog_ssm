package com.hf.common.exception;

//自定义异常
public class HfException extends RuntimeException{

    private Integer code;

    private String body;

    public HfException(){

    }

    public HfException(Integer code, String body){
        this.code = code;
        this.body = body;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
