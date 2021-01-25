package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SpecificationService
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/25
 * @Version V1.0
 **/
@Api(tags = "规格")
public interface SpecificationService {

    @ApiOperation(value = "规格查询")
    @GetMapping(value = "specgroup/list")
    Result<List<SpecGroupEntity>> list(SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格新增")
    @PostMapping(value = "specgroup/save")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格修改")
    @PutMapping(value = "specgroup/save")
    Result<JSONObject> edit(@Validated({MingruiOperation.Update.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格删除")
    @DeleteMapping(value = "specgroup/delete/{id}")
    Result<JSONObject> delete(@PathVariable Integer id);
}
