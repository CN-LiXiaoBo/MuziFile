package com.sicnu.muzifile.factory;

import com.sicnu.muzifile.commom.constant.StorageTypeEnum;
import com.sicnu.muzifile.configuration.MuziFileProperties;
import com.sicnu.muzifile.operation.upload.Uploader;
import com.sicnu.muzifile.operation.upload.product.AliyunOSSUploader;
import com.sicnu.muzifile.operation.upload.product.LocalStorageUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 工厂类
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 14:24
 */
@Component
public class MuziFileFactory {
    @Autowired
    AliyunOSSUploader aliyunOSSUploader;
    @Autowired
    LocalStorageUploader localStorageUploader;

    private String storageType;
    public MuziFileFactory(MuziFileProperties properties){
        this.storageType = properties.getStorageType();
    }
    @Bean
    public Uploader getUploader(){
        int type = Integer.parseInt(storageType);
        Uploader uploader = null;
        if(StorageTypeEnum.Loacl.getCode() == type){
            uploader = localStorageUploader;
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type){
            uploader = aliyunOSSUploader;
        }
        return uploader;
    }
}
