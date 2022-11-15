package com.sicnu.muzifile.operation.upload;

import com.sicnu.muzifile.commom.exception.MuziFileException;
import com.sicnu.muzifile.commom.exception.MuziFileExceptionEnum;
import com.sicnu.muzifile.commom.utils.RedisLockUtils;
import com.sicnu.muzifile.commom.utils.RedisUtils;
import com.sicnu.muzifile.operation.upload.domain.UploadFile;
import com.sicnu.muzifile.operation.upload.domain.UploadFileResult;
import com.sicnu.muzifile.operation.upload.request.MuziMultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author 热爱生活の李
 * @version 1.0
 * @since 2022/11/2 14:42
 */
@Component
@Slf4j
public abstract class Uploader {

    @Resource
    protected RedisUtils redisUtils;
    @Resource
    protected RedisLockUtils redisLockUtils;

    /**
     * 普通上传
     * @param httpServletRequest
     * @return
     */
    public List<UploadFileResult> upload(HttpServletRequest httpServletRequest){
        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(1);
        uploadFile.setChunkSize(0);
        uploadFile.setTotalChunks(1);

        uploadFile.setIdentifier(UUID.randomUUID().toString());
        return upload(httpServletRequest,uploadFile);
    }

    /**
     * 分片上传
     * @param httpServletRequest
     * @param uploadFile
     * @return
     */
    public List<UploadFileResult> upload(HttpServletRequest httpServletRequest,UploadFile uploadFile){
        List<UploadFileResult> uploadFileResultList = new ArrayList<>();

        StandardMultipartHttpServletRequest request = null;
        //强转为StandardMultipartHttpServletRequest
        try {
            request = (StandardMultipartHttpServletRequest) httpServletRequest;
        }catch (ClassCastException e){
            throw new MuziFileException(MuziFileExceptionEnum.NO_FILE_REQUEST);
        }
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(!isMultipart){
            throw new MuziFileException(MuziFileExceptionEnum.NO_FILE_REQUEST);
        }
        try {
            //获取到前端文件参数的名称
            Iterator<String> iter = request.getFileNames();
            while (iter.hasNext()){
                List<MultipartFile> files = request.getFiles(iter.next());
                for (MultipartFile file : files) {
                    MuziMultipartFile muziMultipartFile = new MuziMultipartFile(file);
                    UploadFileResult uploadFileResult = doUploadFlow(muziMultipartFile, uploadFile);
                    uploadFileResultList.add(uploadFileResult);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new MuziFileException(MuziFileExceptionEnum.ERROR_SHARDING_UPLOAD_FILE);
        }
        return uploadFileResultList;
    }
    protected UploadFileResult doUploadFlow(MuziMultipartFile muziMultipartFile,UploadFile uploadFile) throws IOException {
        rectifier(muziMultipartFile,uploadFile);
        UploadFileResult uploadFileResult = organizationalResults(muziMultipartFile, uploadFile);
        return uploadFileResult;
    }

    private void rectifier(MuziMultipartFile muziMultipartFile,UploadFile uploadFile){
        String key = "upload:Identifier" + uploadFile.getIdentifier() + ":lock";
        String cur_upload_chunk_number = "upload:Identifier" + uploadFile.getIdentifier() + ":current_upload_chunk_number";
        redisLockUtils.lock(key);
        try {
            log.error("当前上传第{}块,获得了锁",uploadFile.getChunkNumber());
            if(redisUtils.getObject(cur_upload_chunk_number) == null){
                log.error("当前块{}，去设置获取第一块",uploadFile.getChunkNumber());
                redisUtils.set(cur_upload_chunk_number,"1",1000*60*60);
            }

            int curNumber = Integer.parseInt(redisUtils.getObject(cur_upload_chunk_number));
            log.info("当前需要第{}块,实际来的是第{}块",curNumber,uploadFile.getChunkNumber());

            // 这个是可以保证每次来的都是按照顺序
            if(uploadFile.getChunkNumber() != curNumber){
                log.info("当前上传第{}块,需要第{}块所以释放锁等待",uploadFile.getChunkNumber(),curNumber);
                log.error("当前第{}块释放了锁",uploadFile.getChunkNumber());
                redisLockUtils.unlock(key);
                Thread.sleep(100);
                while (redisLockUtils.tryLock(key,300, TimeUnit.SECONDS)){
                    log.error("当前是第{}块,通过尝试又获取了锁",uploadFile.getChunkNumber());
                    curNumber = Integer.parseInt(redisUtils.getObject(cur_upload_chunk_number));
                    if(uploadFile.getChunkNumber() <= curNumber){
                        log.info("当前是第{}块,需要第{}块",uploadFile.getChunkNumber(),curNumber);
                        break;
                    }else{
                        if(Math.abs(curNumber - uploadFile.getChunkNumber()) > 2){
                            log.error("出错了");
                            throw new MuziFileException();
                        }
                        log.error("当前是第{}块,在尝试中失败获取了锁",uploadFile.getChunkNumber());
                        redisLockUtils.unlock(key);
                    }
                }
            }
            if(uploadFile.getChunkNumber() == curNumber){
                log.info("需要第{}块,第{}块正在上传...",curNumber,uploadFile.getChunkNumber());
                doUploadFileChunk(muziMultipartFile,uploadFile);
                log.info("第{}块上传完毕...",uploadFile.getChunkNumber());
                this.redisUtils.getIncr("upload:Identifier" + uploadFile.getIdentifier() + ":current_upload_chunk_number");
            }
        }catch (Exception e){
            redisUtils.set("upload:Identifier" + uploadFile.getIdentifier() + ":current_upload_chunk_number",String.valueOf(uploadFile.getChunkNumber()),1000*60*60);
        }finally {
            redisLockUtils.unlock(key);
        }
    }

    protected abstract void doUploadFileChunk(MuziMultipartFile muziMultipartFile, UploadFile uploadFile)  throws IOException;
    protected abstract UploadFileResult organizationalResults(MuziMultipartFile muziMultipartFile, UploadFile uploadFile);

    public synchronized boolean checkUploadStatus(UploadFile param, File confFile) throws IOException {
        RandomAccessFile conf = new RandomAccessFile(confFile, "rw");
        try {
            //设置文件长度
            conf.setLength(param.getTotalChunks());
            //设置起始偏移量
            conf.seek(param.getChunkNumber()-1);
            //将指定的一个字节写入
            conf.write(Byte.MAX_VALUE);
        }finally {
            IOUtils.closeQuietly(conf);
        }
        byte[] completeStatus = FileUtils.readFileToByteArray(confFile);
        for (byte b : completeStatus){
            if(b != Byte.MAX_VALUE){
                return false;
            }
        }
        confFile.delete();
        return true;
    }
}
