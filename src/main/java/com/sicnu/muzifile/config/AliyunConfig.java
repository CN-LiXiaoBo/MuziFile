package com.sicnu.muzifile.config;

import com.sicnu.muzifile.domain.AliyunOSS;
import lombok.Data;
/**
 * TODO
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/14 13:02
 */
@Data
public class AliyunConfig {
    private AliyunOSS oss = new AliyunOSS();
}
