package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * 组织架构表(Dict)表服务接口
 *
 * @author makejava
 * @since 2023-05-11 22:29:28
 */
public interface DictService extends IService<Dict> {

    List<Dict> getChildListByPid(Long pid);

    void upload(MultipartFile file) throws IOException;

    void download(HttpServletResponse response) throws IOException;

    String getName(String parentDictCode,String value);
}
