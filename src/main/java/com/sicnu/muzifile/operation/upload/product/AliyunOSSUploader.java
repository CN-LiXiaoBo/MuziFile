package com.sicnu.muzifile.operation.upload.product;

import com.alibaba.fastjson2.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.sicnu.muzifile.commom.constant.StorageTypeEnum;
import com.sicnu.muzifile.commom.utils.RedisUtils;
import com.sicnu.muzifile.config.AliyunConfig;
import com.sicnu.muzifile.operation.upload.Uploader;
import com.sicnu.muzifile.operation.upload.domain.UploadFile;
import com.sicnu.muzifile.operation.upload.domain.UploadFileInfo;
import com.sicnu.muzifile.operation.upload.domain.UploadFileResult;
import com.sicnu.muzifile.operation.upload.request.MuziMultipartFile;
import com.sicnu.muzifile.commom.utils.AliyunUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * TODO
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 14:47
 */
@Component
public class AliyunOSSUploader extends Uploader {
    @Autowired
    protected RedisUtils redisUtils;
    protected AliyunConfig aliyunConfig;
    public AliyunOSSUploader(){

    }
    public AliyunOSSUploader(AliyunConfig aliyunConfig){
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    protected void doUploadFileChunk(MuziMultipartFile muziMultipartFile, UploadFile uploadFile) throws IOException {
        OSS osSclient = AliyunUtils.getOSSclient(aliyunConfig);
        try {
            UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtils.getObject("upload:Identifier" + uploadFile.getIdentifier()+":uploadPartRequest"), UploadFileInfo.class);
            String fileUrl = muziMultipartFile.getFileUrl();
            //说明是第一片上传
            if(uploadFileInfo == null){
                InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(aliyunConfig.getOss().getBucket(),fileUrl);
                InitiateMultipartUploadResult uploadResult = osSclient.initiateMultipartUpload(request);
                //这个uploadId相当于这个分片文件在oss中保存的唯一标识
                String uploadId = uploadResult.getUploadId();

                uploadFileInfo = new UploadFileInfo();
                uploadFileInfo.setBucketName(aliyunConfig.getOss().getBucket());
                uploadFileInfo.setKey(fileUrl);
                uploadFileInfo.setUploadId(uploadId);
                redisUtils.set("upload:Identifier" + uploadFile.getIdentifier()+":uploadPartRequest",JSON.toJSONString(uploadFileInfo));
            }

            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(uploadFileInfo.getBucketName());
            uploadPartRequest.setKey(uploadFileInfo.getKey());
            uploadPartRequest.setUploadId(uploadFileInfo.getUploadId());
            uploadPartRequest.setInputStream(muziMultipartFile.getUploadInputStream());
            uploadPartRequest.setPartSize(muziMultipartFile.getSize());
            uploadPartRequest.setPartNumber(uploadFile.getChunkNumber());

            UploadPartResult uploadPartResult = osSclient.uploadPart(uploadPartRequest);

            if(redisUtils.hasKey("upload:Identifier"+uploadFile.getIdentifier()+":partEtags")){
                List<PartETag> partETags = JSON.parseArray(redisUtils.getObject("upload:Identifier" + uploadFile.getIdentifier() + ":partEtags"), PartETag.class);
                partETags.add(uploadPartResult.getPartETag());
                redisUtils.set("upload:Identifier"+uploadFile.getIdentifier()+":partEtags",JSON.toJSONString(partETags));
            }else {
                List<PartETag> partETags = new ArrayList<>();
                partETags.add(uploadPartResult.getPartETag());
                redisUtils.set("upload:Identifier"+uploadFile.getIdentifier()+":partEtags",JSON.toJSONString(partETags));
            }
        }finally {
            osSclient.shutdown();
        }
    }

    @Override
    protected UploadFileResult organizationalResults(MuziMultipartFile muziMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();
        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtils.getObject("upload:Identifier" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        uploadFileResult.setFileUrl(uploadFileInfo.getKey());
        uploadFileResult.setFileName(muziMultipartFile.getFileName());
        uploadFileResult.setExtendName(muziMultipartFile.getExtendName());
        uploadFileResult.setFileSize(uploadFile.getTotalSize());
        if(uploadFile.getTotalChunks() == 1){
            uploadFileResult.setFileSize(muziMultipartFile.getSize());
        }
        uploadFileResult.setStorageType(StorageTypeEnum.ALIYUN_OSS);
        uploadFileResult.setIdentifier(uploadFile.getIdentifier());
        if(uploadFile.getChunkNumber() == uploadFile.getTotalChunks()){
            completeMultipartUpload(uploadFile);
        }
        return uploadFileResult;
    }
    private void completeMultipartUpload(UploadFile uploadFile) {

        List<PartETag> partETags = JSON.parseArray(redisUtils.getObject("upload:Identifier" + uploadFile.getIdentifier() + ":partEtags"), PartETag.class);
        partETags.sort(Comparator.comparingInt(PartETag::getPartNumber));

        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtils.getObject("upload:Identifier" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(aliyunConfig.getOss().getBucket(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId(),
                        partETags);
        OSS ossClient = AliyunUtils.getOSSclient(aliyunConfig);
        // 完成上传。
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        ossClient.shutdown();

    }
}
