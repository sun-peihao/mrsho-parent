package com.baidu.shop.base;

/**
 * @ClassName Result
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/19
 * @Version V1.0
 **/
public class Result<T> {

    private Integer code;//返回码

    private String message;//返回消息

    private T data;//返回数据

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = (T) data;
    }
}
