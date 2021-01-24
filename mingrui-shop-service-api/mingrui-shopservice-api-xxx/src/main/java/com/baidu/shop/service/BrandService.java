package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName BrandService
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/21
 * @Version V1.0
 **/
public interface BrandService {
    @ApiOperation(value = "获取品牌信息")
    @GetMapping(value = "brand/getBrandInfo")
    Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO);

    @ApiOperation(value = "新增品牌")
    @PostMapping(value = "brand/addBrandInfo")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class})@RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "修改品牌")
    @PutMapping(value = "brand/save")
    Result<JSONObject> editBrand(@RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "删除品牌")
    @DeleteMapping(value = "brand/delBrand")
    Result<JSONObject> deleteBrand(Integer brandId);
}
