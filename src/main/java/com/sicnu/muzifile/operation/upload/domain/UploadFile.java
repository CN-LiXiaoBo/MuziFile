package com.sicnu.muzifile.operation.upload.domain;

import lombok.Data;

/**
 * 分片上传相关参数
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 14:50
 */
@Data
public class UploadFile {

    //当前分片号
    private int chunkNumber;
    //分块上限大小
    private long chunkSize;
    //总分片数量
    private int totalChunks;
    //文件标识
    private String identifier;
    //文件中总大小
    private long totalSize;
    //当前这块的分片大小
    private long currentChunkSize;
}
