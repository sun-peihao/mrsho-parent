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
}
