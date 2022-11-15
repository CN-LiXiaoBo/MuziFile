package com.sicnu.muzifile.configuration;

import com.sicnu.muzifile.commom.utils.MuziUtils;
import com.sicnu.muzifile.commom.utils.RedisLockUtils;
import com.sicnu.muzifile.commom.utils.RedisUtils;
import com.sicnu.muzifile.factory.MuziFileFactory;
import com.sicnu.muzifile.operation.upload.product.AliyunOSSUploader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/14 13:16
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MuziFileProperties.class)
public class AutoConfiguration {
    @Resource
    private MuziFileProperties muziFileProperties;

    @Bean
    public MuziFileFactory muziFileFactory(){
        MuziUtils.LOCAL_STORAGE_PATH = muziFileProperties.getLocalStoragePath();
        String bucketName = muziFileProperties.getBucketName();
        if(StringUtils.isNotEmpty(bucketName)){
            MuziUtils.ROOT_PATH = bucketName;
        }else{
            MuziUtils.ROOT_PATH = "upload";
        }

        return new MuziFileFactory(muziFileProperties);
    }

    @Bean
    public AliyunOSSUploader aliyunOSSUploader(){
        return new AliyunOSSUploader(muziFileProperties.getAliyun());
    }

    @Bean
    public RedisUtils redisUtils(){
        return new RedisUtils();
    }

    @Bean
    public RedisLockUtils redisLockUtils(){return new RedisLockUtils();}
}
