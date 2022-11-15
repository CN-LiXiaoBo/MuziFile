package com.sicnu.muzifile.configuration;

import com.sicnu.muzifile.config.AliyunConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 读取配置文件
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 14:29
 */
@Data
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "muzifile")
public class MuziFileProperties {
    private String bucketName;
    private String storageType;
    private String localStoragePath;
    private AliyunConfig aliyun = new AliyunConfig();
}
