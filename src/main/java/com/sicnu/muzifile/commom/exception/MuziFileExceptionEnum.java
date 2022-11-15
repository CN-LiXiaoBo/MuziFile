package com.sicnu.muzifile.commom.exception;


/**
 * 自定义异常枚举类
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 14:07
 */
public enum MuziFileExceptionEnum {
    NO_FILE_REQUEST(10001,"request请求未包含文件"),
    CREATE_MKDIR_TEMP_ERROR(10002,"创建temp目录失败"),
    // 上传文件错误
    ERROR_SHARDING_UPLOAD_FILE(20000,"分片文件上传出错")

    ;
    private int code;
    private String message;
    MuziFileExceptionEnum(int code,String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
