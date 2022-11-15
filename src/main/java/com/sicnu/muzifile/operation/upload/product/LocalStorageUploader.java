package com.sicnu.muzifile.operation.upload.product;

import com.sicnu.muzifile.commom.constant.StorageTypeEnum;
import com.sicnu.muzifile.commom.constant.UploadFileStatusEnum;
import com.sicnu.muzifile.commom.utils.MuziUtils;
import com.sicnu.muzifile.operation.upload.Uploader;
import com.sicnu.muzifile.operation.upload.domain.UploadFile;
import com.sicnu.muzifile.operation.upload.domain.UploadFileResult;
import com.sicnu.muzifile.operation.upload.request.MuziMultipartFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 14:46
 */
@Component
public class LocalStorageUploader extends Uploader {
    public static Map<String,String> FILE_URL_MAP = new HashMap<>();
    @Override
    protected UploadFileResult doUploadFlow(MuziMultipartFile muziMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();
        //打开将要写入的文件
        try {
            String fileUrl = MuziUtils.getUploadFileUrl(uploadFile.getIdentifier(), muziMultipartFile.getExtendName());
            if(StringUtils.isNotBlank(FILE_URL_MAP.get(uploadFile.getIdentifier()))){
                fileUrl = FILE_URL_MAP.get(uploadFile.getIdentifier());
            }else {
                FILE_URL_MAP.put(uploadFile.getIdentifier(),fileUrl);
            }
            String tempFileUrl = fileUrl + "_tmp";
            String confFileUrl = fileUrl.replace("."+muziMultipartFile.getExtendName(),".conf");

            File file = new File(MuziUtils.getStaticPath()+fileUrl);
            File tempFile = new File(MuziUtils.getStaticPath()+tempFileUrl);
            File confFile = new File(MuziUtils.getStaticPath()+confFileUrl);


            //第一步打开将要写的文件
            RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
            //第二步 打开通道
            try {
                FileChannel channel = raf.getChannel();
                //第三步 计算偏移量
                long position = (uploadFile.getChunkNumber()-1)*uploadFile.getChunkSize();
                //第四步 获取分片数据
                byte[] fileData = muziMultipartFile.getUploadBytes();
                //第五步 写入数据
                channel.position(position);
                channel.write(ByteBuffer.wrap(fileData));
                channel.force(true);
                channel.close();
            }catch (Exception e){

            }finally {
                IOUtils.closeQuietly(raf);
            }
            // 判断是否完成文件的传输并进行校验与重命名
            boolean isComplete = checkUploadStatus(uploadFile, confFile);
            uploadFileResult.setFileUrl(fileUrl);
            uploadFileResult.setFileName(muziMultipartFile.getFileName());
            uploadFileResult.setExtendName(muziMultipartFile.getExtendName());
            uploadFileResult.setFileSize(uploadFile.getTotalSize());
            uploadFileResult.setStorageType(StorageTypeEnum.Loacl);
            if(uploadFile.getTotalChunks() == 1){
                uploadFileResult.setFileSize(muziMultipartFile.getSize());
            }
            uploadFileResult.setIdentifier(uploadFile.getIdentifier());
            if(isComplete){
                tempFile.renameTo(file);
                FILE_URL_MAP.remove(uploadFile.getIdentifier());
                uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
            }else{
                uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLETE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uploadFileResult;
    }

    @Override
    protected void doUploadFileChunk(MuziMultipartFile muziMultipartFile, UploadFile uploadFile) throws IOException {

    }

    @Override
    protected UploadFileResult organizationalResults(MuziMultipartFile muziMultipartFile, UploadFile uploadFile) {
        return null;
    }
}
