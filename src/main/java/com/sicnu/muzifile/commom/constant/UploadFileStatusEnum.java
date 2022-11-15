package com.sicnu.muzifile.commom.constant;

/**
 * 文件上传状态码
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 13:55
 */
public enum UploadFileStatusEnum {
    FAIL(0,"上传失败"),
    SUCCESS(1,"上传成功"),
    UNCOMPLETE(2,"未完成");

    private int code;
    private String message;
    UploadFileStatusEnum(int code,String message){
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
