package com.example.waimai.common;
//自定义业务异常
public class CustomerException extends RuntimeException{
    public CustomerException(String message){
        super(message);
    }
}
