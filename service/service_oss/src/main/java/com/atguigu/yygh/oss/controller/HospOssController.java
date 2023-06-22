package com.atguigu.yygh.oss.controller;

import com.atguigu.yygh.oss.service.HospOssService;
import com.atguigu.yygh.result.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 阿里云oss储存，控制器
 */
@RestController
@RequestMapping("/user/oss/file")
public class HospOssController {
    @Resource
    private HospOssService hospOssService;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @ApiOperation(value = "文件上传")
    @PostMapping("upload")
    public R upload(@RequestParam("file") MultipartFile file){
        String url = hospOssService.upload(file);
        return R.ok().message("文件上传成功").data("url",url);
    }
}
