package com.baidu.shop.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.utils.ObjectUtil;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/19
 * @Version V1.0
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setParentId(pid);

        List<CategoryEntity> list = categoryMapper.select(categoryEntity);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<JsonObject> delCategory(Integer id) {
        if (ObjectUtil.isNotNull(id) || id > 0){

            //通过id查询当前节点信息
            CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);

            //判断当前节点是否是父节点
            if (categoryEntity.getIsParent() == 1) return this.setResultError("当前节点为父节点");

            Example example = new Example(CategoryEntity.class);
            example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());

            //通过当前节点的父节点的id 查询当前节点的父节点下是否还有其他子节点
            List<CategoryEntity> categoryList = categoryMapper.selectByExample(example);

            if(categoryList.size() <= 1){
                CategoryEntity categoryEntity1 = new CategoryEntity();
                categoryEntity1.setIsParent(0);
                categoryEntity1.setId(categoryEntity.getParentId());


                categoryMapper.updateByPrimaryKeySelective(categoryEntity1);
            }

            categoryMapper.deleteByPrimaryKey(categoryEntity);
            return this.setResultSuccess();
        }

        return this.setResultError("id不符合");
    }

    @Override
    public Result<JSONObject> editCategory(CategoryEntity entity) {
        categoryMapper.updateByPrimaryKeySelective(entity);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> addCategory(CategoryEntity entity) {
        categoryMapper.insertSelective(entity);
        return this.setResultSuccess();
    }
}
