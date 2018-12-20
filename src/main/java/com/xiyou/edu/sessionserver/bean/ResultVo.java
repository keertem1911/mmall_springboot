package com.xiyou.edu.sessionserver.bean;

import lombok.Data;

@Data
public class ResultVo<T> {
    private int code;
    private String msg;
    private T data;

    public ResultVo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public ResultVo(int code) {
        this.code = code;
    }

    public ResultVo(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public static ResultVo success(){
        return new ResultVo(0,"");
    }
    public static <T> ResultVo<T> success(T data){
        return new ResultVo<T>(0,data);
    }
    public static ResultVo error(int code,String msg){
        return new ResultVo(code,msg);
    }

}
