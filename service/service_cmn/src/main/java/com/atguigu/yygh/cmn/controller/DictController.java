package com.atguigu.yygh.cmn.controller;


import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.result.R;
import com.baomidou.mybatisplus.extension.api.ApiController;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 组织架构表(Dict)表控制层
 *
 * @author makejava
 * @since 2023-05-11 22:29:26
 */
@RestController
@RequestMapping("/admin/cmn")
//@CrossOrigin //解决局部跨域问题
public class DictController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private DictService dictService;

    /**
     * 根据pid获取次级数据
     * @param pid
     * @return
     */
    @GetMapping("/childList/{pid}")
    public R getChildListByPid(@PathVariable Long pid){
        List<Dict> list = dictService.getChildListByPid(pid);
        return R.ok().data("items",list);
    }

    /**
     * 上传excel文件
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public R upload(MultipartFile file) throws IOException {
        dictService.upload(file);
        return R.ok();
    }

    /**
     * 下载excel文件
     * @param response
     * @throws IOException
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        dictService.download(response);
    }

    /**
     * 根据value获取数据字典名字
     * @param value
     * @return
     */
    @ApiOperation(value = "获取数据字典名称")
    @GetMapping("/getName/{value}")
    public String getName(@PathVariable("value") String value){
        return dictService.getName("",value);
    }

    /**
     * 根据praentDictCode和value获取数据字典名字
     * @param parentDictCode
     * @param value
     * @return
     */
    @ApiOperation(value = "获取数据字典名称")
    @GetMapping("/getName/{parentDictCode}/{value}")
    public String getName(@PathVariable("parentDictCode") String parentDictCode,@PathVariable("value") String value){
        return dictService.getName(parentDictCode,value);
    }
}

