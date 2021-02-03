package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName GoodsService
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/2/3
 * @Version V1.0
 **/
@Api(tags = "商品接口")
public interface GoodsService {

    @ApiOperation(value = "获取spu信息")
    @GetMapping(value = "goods/getSpuInfo")
    Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO);

    @ApiOperation(value = "新增商品")
    @PostMapping(value = "goods/save")
    Result<JSONUtil> saveGoods(@Validated({MingruiOperation.Add.class}) @RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "通过spuid获取spuDetail信息")
    @GetMapping(value = "goods/getSpuDetailBySpuId")
    Result<List<SpuEntity>> getSpuDetailBySpuId(Integer spuId);

    @ApiOperation(value = "通过spuid获取sku信息")
    @GetMapping(value = "goods/getSkuBySpuId")
    Result<List<SkuDTO>> getSkuBySpuId(Integer spuId);

    @ApiOperation(value = "修改商品")
    @PutMapping(value = "goods/save")
    Result<JSONUtil> editGoods(@Validated({MingruiOperation.Update.class}) @RequestBody SpuDTO spuDTO);
}
