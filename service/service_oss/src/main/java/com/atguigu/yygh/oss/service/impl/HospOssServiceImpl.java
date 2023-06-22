package com.atguigu.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.yygh.oss.prop.OssProperties;
import com.atguigu.yygh.oss.service.HospOssService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class HospOssServiceImpl implements HospOssService {

    @Resource
    private OssProperties ossProperties;
    @Override
    public String upload(MultipartFile file){
        //从配置类获取oss参数
        String endpoint = ossProperties.getEndpoint();
        String bucketname = ossProperties.getBucketname();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();


        try{
            //获取oss客户端
            OSS ossClient = new OSSClientBuilder().build(endpoint,keyid,keysecret);

            //获取上传文件流
            InputStream fileInputStream = file.getInputStream();

            //获取文件名，并用UUID拼接
            String filename = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            filename = filename + uuid;

            //按照当前日期，创建文件夹，上传到创建文件夹里面
            String timeUrl = new DateTime().toString("yyyy/MM/dd");
            filename = timeUrl+"/"+filename;

            //调用方法实现上传
            ossClient.putObject(bucketname, filename, fileInputStream);
            // 关闭OSSClient。
            ossClient.shutdown();
            //上传之后文件路径
            // https://yygh-atguigu.oss-cn-beijing.aliyuncs.com/01.jpg
            String url = "https://"+bucketname+"."+endpoint+"/"+filename;
            //返回
            return url;

        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
