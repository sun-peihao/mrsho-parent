package com.baidu.shop.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.service.SpecificationService;
import com.baidu.shop.utils.BaiduBeanUtil;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecificationServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/25
 * @Version V1.0
 **/
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {

    @Resource
    private SpecGroupMapper specificationMapper;

    @Override
    public Result<List<SpecGroupEntity>> list(SpecGroupDTO specGroupDTO) {
        Example example = new Example(SpecGroupEntity.class);
        example.createCriteria().andEqualTo("cid",
                BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class).getCid());
        List<SpecGroupEntity> list = specificationMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<JSONObject> save(SpecGroupDTO specGroupDTO) {
        specificationMapper.insertSelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> edit(SpecGroupDTO specGroupDTO) {

        specificationMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delete(Integer id) {

        specificationMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }
}
