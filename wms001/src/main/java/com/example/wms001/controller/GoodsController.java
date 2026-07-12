package com.example.wms001.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.wms001.common.FileUploadUtil;
import com.example.wms001.common.QueryQageParam;
import com.example.wms001.common.Result;
import com.example.wms001.entity.Goods;
import com.example.wms001.service.IGoodsService;
import com.example.wms001.service.IGoodstypeService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author buyaola
 * @since 2026-03-24
 */
@RestController
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    // 文件上传和下载  地址  请求地址
    @Value("${upload.goods.base-path}")
    private String basePath;
    @Value("${upload.goods.access-prefix}")
    private String accessPrefix;

    @Autowired
    private IGoodsService goodsService;

    /**
     * 商品图片上传接口
     * 前端 FormData 字段名必须是 "image"，和你前端的 formData.append('image', ...) 匹配
     */
    @PostMapping("/uploadPicture")
    public Map<String, Object> uploadPicture(@RequestParam("image") MultipartFile file) {
        log.info("开始上传图片...");
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 调用工具类，把文件写入服务器硬盘
            String fileName = FileUploadUtil.uploadToLocal(file, basePath);

            // 2. 生成访问路径（存入数据库 img 字段的就是这个）
            // 例如：/upload/goods/abc123.jpg
            String imgUrl = accessPrefix + fileName;

            // 3. 把 imgUrl 存到你的 goods 表的 img 字段
            // 这里你需要调用自己的 Service 层更新数据库
            // goodsService.updateGoodsImg(goodsId, imgUrl);

            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("imgUrl", imgUrl); // 返回给前端预览用
        } catch (IOException e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "上传失败：" + e.getMessage());
        }
        return result;
    }

    //新增
    @PostMapping("/save")
    public Result save(@RequestBody Goods goods) {
        return  goodsService.save(goods)?Result.suc():Result.fail();
    }
    //删除
    @GetMapping("/delete")
    public Result delete(Integer id) {
        return  goodsService.removeById(id)?Result.suc():Result.fail();
    }

    //修改
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        return  goodsService.updateById(goods)?Result.suc():Result.fail();
    }

    //获取所有商品列表（用于前台展示）
    @GetMapping("/list")
    public Result list() {
        List<Goods> goodsList = goodsService.list();
        return Result.suc(goodsList);
    }

    //查询 （模糊，匹配）
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryQageParam query) {
        HashMap param = query.getParam();
        String name = (String)param.get("name");
        String goodstype = String.valueOf(param.get("goodsType"));
        String storage = String.valueOf(param.get("storage"));

        Page<Goods> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Goods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(Goods::getName,name);
        }
        if(StringUtils.isNotBlank(goodstype) && !"null".equals(goodstype)){
            lambdaQueryWrapper.eq(Goods::getGoodsType,goodstype);
        }
        if(StringUtils.isNotBlank(storage) && !"null".equals(storage)){
            lambdaQueryWrapper.eq(Goods::getStorage,storage);
        }

        IPage result = goodsService.pageCC(page, lambdaQueryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }

}
