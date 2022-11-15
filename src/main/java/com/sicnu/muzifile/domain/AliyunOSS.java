package com.sicnu.muzifile.domain;

import lombok.Data;

/**
 * TODO
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/14 13:09
 */
@Data
public class AliyunOSS {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String objectName;
}
