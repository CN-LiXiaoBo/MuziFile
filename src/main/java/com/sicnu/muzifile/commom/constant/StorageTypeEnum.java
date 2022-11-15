package com.sicnu.muzifile.commom.constant;


/**
 * 文件上传位置枚举
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 13:55
 */
public enum StorageTypeEnum {

    Loacl(0,"本地存储"),
    ALIYUN_OSS(1,"阿里云OSS对象存储");
    private int code;
    private String name;
    StorageTypeEnum(int code,String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
