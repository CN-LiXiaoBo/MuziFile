package com.sicnu.muzifile.commom.utils;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.sicnu.muzifile.config.AliyunConfig;

/**
 * TODO
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/14 13:43
 */
public class AliyunUtils {
    public static OSS getOSSclient(AliyunConfig aliyunConfig){
        return new OSSClientBuilder().build(aliyunConfig.getOss().getEndpoint(),
                aliyunConfig.getOss().getAccessKey(),
                aliyunConfig.getOss().getSecretKey());
    }
}
