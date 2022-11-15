package com.sicnu.muzifile.operation.upload.request;

import com.sicnu.muzifile.commom.utils.MuziUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * TODO
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 14:55
 */
public class MuziMultipartFile {
    MultipartFile multipartFile = null;
    public MuziMultipartFile(){

    }
    public MuziMultipartFile(MultipartFile multipartFile){
        this.multipartFile = multipartFile;
    }
    public String getFileName(){
        String originalFilename = getMultipartFile().getOriginalFilename();
        if(!originalFilename.contains(".")){
            return originalFilename;
        }
        return originalFilename.substring(0,originalFilename.lastIndexOf("."));
    }
    public String getExtendName(){
        String originalFilename = getMultipartFile().getOriginalFilename();
        return FilenameUtils.getExtension(originalFilename);
    }
    public String getFileUrl(){
        String uuid = UUID.randomUUID().toString();
        String uploadFileUrl = MuziUtils.getUploadFileUrl(uuid, getExtendName());
        return uploadFileUrl;
    }
    public String getFileUrl(String identify) {
        String fileUrl = MuziUtils.getUploadFileUrl(identify, getExtendName());
        return fileUrl;
    }
    public InputStream getUploadInputStream() throws IOException {
        return getMultipartFile().getInputStream();
    }
    public byte[] getUploadBytes() throws IOException {
        return getMultipartFile().getBytes();
    }
    public long getSize() {
        long size = getMultipartFile().getSize();
        return size;
    }
    public MultipartFile getMultipartFile() {
        return multipartFile;
    }
}
