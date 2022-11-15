package com.sicnu.muzifile.commom.exception;

/**
 * 自定义异常
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 14:07
 */
public class MuziFileException extends RuntimeException{
    private int code = 500;
    private String message = "muzi-file出现问题";
    public MuziFileException(int code,String message){
        this.code = code;
        this.message = message;
    }
    public MuziFileException(){

    }
    public MuziFileException(String message){
        this.message = message;
    }
    public MuziFileException(MuziFileExceptionEnum exceptionEnum){
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
