package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.util.List;

/**
 * 组织架构表(Dict)表服务实现类
 *
 * @author makejava
 * @since 2023-05-11 22:29:29
 */
@Service("dictService")
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    @Cacheable(value = "Dict")
    public List<Dict> getChildListByPid(Long pid) {
        LambdaQueryWrapper<Dict> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dict::getParentId,pid);
        List<Dict> dictList = baseMapper.selectList(lqw);
        for (Dict dict : dictList){ //判断是否有次级数据
            boolean flag = isHasChildren(dict.getId());
            dict.setHasChildren(flag);
        }
        return dictList;
    }

    @Override
    @CacheEvict(value = "Dict", allEntries=true)
    public void upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper)).sheet(0).doRead();
    }

    @Override
    public void download(HttpServletResponse response) throws IOException {
        List<Dict> list =baseMapper.selectList(null);
        List<DictEeVo> dictEeVoList = new ArrayList<DictEeVo>(list.size());
        for (Dict dict : list) {
            DictEeVo dictEeVo=new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);//要求源对象dict和目标对象dictEeVo对应的属性名必须相同
            dictEeVoList.add(dictEeVo);
        }
        //下载：响应头信息
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("字典文件", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        //这么写有没有问题？
        EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("字典数据").doWrite(dictEeVoList);
    }

    @Override
    public String getName(String parentDictCode ,String value) {
        //parentDictCode为空，即为查询地区相关,否则为查询医院等级
        if(StringUtils.isEmpty(parentDictCode)){
            LambdaQueryWrapper<Dict> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(Dict::getValue,value);
            Dict dict = baseMapper.selectOne(lqw1);
            return dict.getName();
        }else {
            Dict parentDict = getDictByDictCode(parentDictCode);
            //父字典为空，返回空
            if(null == parentDict) return "";
            LambdaQueryWrapper<Dict> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(Dict::getParentId,parentDict.getId()).eq(Dict::getValue,value);
            Dict dict = baseMapper.selectOne(lqw2);
            if (null != dict){
                return dict.getName();
            }
        }
        return "";
    }

    //根据字典类型查询id
    public Dict getDictByDictCode(String parentDictCode){
        LambdaQueryWrapper<Dict> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dict::getDictCode,parentDictCode);
        return baseMapper.selectOne(lqw);
    }

    private boolean isHasChildren(Long pid) {
        LambdaQueryWrapper<Dict> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dict::getParentId,pid);
        Integer count = baseMapper.selectCount(lqw);
        return count > 0;
    }
}
