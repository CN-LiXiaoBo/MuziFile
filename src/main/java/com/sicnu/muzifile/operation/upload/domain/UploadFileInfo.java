package com.sicnu.muzifile.operation.upload.domain;

import lombok.Data;

/**
 * TODO
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 15:46
 */
@Data
public class UploadFileInfo {
    private String bucketName;
    private String key;
    private String uploadId;
}
