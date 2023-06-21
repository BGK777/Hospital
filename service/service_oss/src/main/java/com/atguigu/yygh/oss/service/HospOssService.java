package com.atguigu.yygh.oss.service;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.web.multipart.MultipartFile;

public interface HospOssService {
    String upload(MultipartFile file);
}
