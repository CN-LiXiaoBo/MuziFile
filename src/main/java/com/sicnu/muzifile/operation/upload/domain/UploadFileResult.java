package com.sicnu.muzifile.operation.upload.domain;

import com.sicnu.muzifile.commom.constant.StorageTypeEnum;
import com.sicnu.muzifile.commom.constant.UploadFileStatusEnum;
import lombok.Data;

/**
 * TODO
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 15:46
 */
@Data
public class UploadFileResult {
    private String fileName;
    private String extendName;
    private long fileSize;
    private String fileUrl;
    private String identifier;
    private StorageTypeEnum storageType;
    private UploadFileStatusEnum status;
}
