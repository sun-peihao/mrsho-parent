package com.baidu.shop.entity;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @ClassName CategoryBrandEntity
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/22
 * @Version V1.0
 **/
@ApiModel(value = "分类品牌中间类")
@Data
@Table(name = "tb_category_brand")
public class CategoryBrandEntity {

    @ApiModelProperty(value = "商品类目id",example = "1")
    @NotNull(message = "商品类目id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer categoryId;

    @ApiModelProperty(value = "品牌id",example = "1")
    @NotNull(message = "品牌id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer brandId;

}
