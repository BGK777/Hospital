package com.atguigu.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface HospOssService {
    String upload(MultipartFile file);
}
