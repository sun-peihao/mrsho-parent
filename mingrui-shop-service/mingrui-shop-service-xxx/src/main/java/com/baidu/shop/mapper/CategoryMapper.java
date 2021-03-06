package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryEntity;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName CategoryMapper
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/19
 * @Version V1.0
 **/
public interface CategoryMapper extends Mapper<CategoryEntity> , SelectByIdListMapper<CategoryEntity,Integer> {
}
