package com.baidu.shop.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/21
 * @Version V1.0
 **/
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {
        //分页
        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        if (!StringUtils.isEmpty(brandDTO.getSort())){
            PageHelper.orderBy(brandDTO.getOrderBy());
        }

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        Example example = new Example(BrandEntity.class);
        example.createCriteria().andLike("name","%"+ brandEntity.getName() + "%");

        List<BrandEntity> brandEntities = brandMapper.selectByExample(example);

        PageInfo<BrandEntity> pageInfo = new PageInfo<>(brandEntities);

        return this.setResultSuccess(pageInfo);
    }

    @Override
    public Result<JSONObject> save(BrandDTO brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO,BrandEntity.class);
        //品牌首字母
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]),false).toCharArray()[0]);
        //通用mapper新增返回主键
        brandMapper.insertSelective(brandEntity);
        //绑定关系
        if(StringUtils.isEmpty(brandDTO.getCategories())){
            return this.setResultError("分类数据不能为空");
        }
        if(brandDTO.getCategories().contains(",")){
            List<CategoryBrandEntity> list = new ArrayList<>();
            String[] categoryArr = brandDTO.getCategories().split(",");
            Arrays.asList(categoryArr).stream().forEach(str -> {
                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                categoryBrandEntity.setBrandId(brandEntity.getId());
                categoryBrandEntity.setCategoryId(Integer.parseInt(str));
                list.add(categoryBrandEntity);
            });
            categoryBrandMapper.insertList(list);
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> editBrand(BrandDTO brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]),false).toCharArray()[0]);
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        this.deleteCategoryBrandByBrandId(brandEntity.getId());

        this.insertCategoryBrandData(brandDTO.getCategories(),brandEntity.getId());

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteBrand(Integer brandId) {

        //删除品牌
        brandMapper.deleteByPrimaryKey(brandId);

        //通过品牌id删除分类
        this.deleteCategoryBrandByBrandId(brandId);

        return this.setResultSuccess();

    }

    private void deleteCategoryBrandByBrandId(Integer id){
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }

    private void insertCategoryBrandData(String categories,Integer id){
        if (StringUtils.isEmpty(categories)) throw new RuntimeException("分类不能为空");
//        List<CategoryBrandEntity> categoryBrandEntities = new ArrayList<>();
        if(categories.contains(",")){
            String[] categoryArray = categories.split(",");
            /*for (String s : categoryArray) {
                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                categoryBrandEntity.setBrandId(brandEntity.getId());
                categoryBrandEntity.setCategoryId(Integer.valueOf(s));
                categoryBrandEntities.add(categoryBrandEntity);
            }*/
            List<CategoryBrandEntity> collect = Arrays.asList(categoryArray).stream().map(categoryIdStr -> {
                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                categoryBrandEntity.setBrandId(id);
                categoryBrandEntity.setCategoryId(Integer.valueOf(categoryIdStr));
                return categoryBrandEntity;
            }).collect(Collectors.toList());
            categoryBrandMapper.insertList(collect);
            /*categoryBrandMapper.insertList(
                Arrays.asList(categoryArray)
                    .stream()
                    .map(categoryIdStr -> new CategoryBrandEntity(Integer.valueOf(categoryIdStr),brandEntity.getId()))
                    .collect(Collectors.toList())
            );*/
        }else{
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(id);
            categoryBrandEntity.setCategoryId(Integer.valueOf(categories));
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }
}
