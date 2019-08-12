package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 用户上传文件
     * @param file
     * @param path
     * @return
     */
    public String upload(MultipartFile file,String path){
        //获取原始文件名(带格式后缀)
        String fileName=file.getOriginalFilename();
        /*获取扩展名
        例如fileName=abc.jpg经过处理后fileExtensionName=jpg
        */
        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        //上传文件名,通过uuid生成一个随机数通过拼接完成上传文件名的制造
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtensionName;
        //打印一行日志
        logger.info("开始上传文件,上传文件名为:{},上传路径为:{},新文件名为:{}",fileName,path,uploadFileName);
        //创建文件
        File fileDir=new File(path);
        if (!fileDir.exists()){//如果文件不存在
            fileDir.setWritable(true);//开启写权限
            fileDir.mkdirs();//创建多层目录
        }
        //创建上传文件
        File targetFile=new File(path,uploadFileName);
        try {
            //文件夹已经上传成功
            file.transferTo(targetFile);
            // 将上传的文件传到服务器
            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //上传完成后,删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
        }
        return targetFile.getName();
    }
}
