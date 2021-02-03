package com.baidu.shop.serviceImpl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/2/3
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private StockMapper stockMapper;

    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {
        if (ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows())) {
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        }

        if (!StringUtils.isEmpty(spuDTO.getSort()) && !StringUtils.isEmpty(spuDTO.getOrder())) {
            PageHelper.orderBy(spuDTO.getOrderBy());
        }

        Example example = new Example(SpuEntity.class);

        if (ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() < 2){
            example.createCriteria().andEqualTo("saleable",spuDTO.getSaleable());
        }

        if (!StringUtils.isEmpty(spuDTO.getTitle())){
            example.createCriteria().andLike("title","%"+spuDTO.getTitle()+"%");
        }

        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);

        List<SpuDTO> spuDTOList = spuEntities.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);
            List<Integer> cidList = Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3());
            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(cidList);
            String categoryName = categoryEntities.stream().map(categoryEntity -> categoryEntity.getName()).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(categoryName);

            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spuEntity.getBrandId());
            spuDTO1.setBrandName(brandEntity.getName());

            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> spuEntityPageInfo = new PageInfo<>(spuEntities);

        return this.setResult(HTTPStatus.OK,spuEntityPageInfo.getTotal() + "",spuDTOList);
    }

    @Override
    public Result<JSONUtil> saveGoods(SpuDTO spuDTO) {
        //spu
        final Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);

        //spuDetail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDetail, SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);

        //sku
        this.saveSkusAndStock(spuDTO,spuEntity.getId(),date);

        return this.setResultSuccess();
    }

    private void saveSkusAndStock(SpuDTO spuDTO,Integer spuId,Date date){
        List<SkuDTO> skus = spuDTO.getSkus();
        skus.stream().forEach(skuDTO -> {
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }

    @Override
    public Result<List<SpuEntity>> getSpuDetailBySpuId(Integer spuId) {

        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);

        return this.setResultSuccess(spuDetailEntity);
    }

    @Override
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId) {

        List<SkuDTO> list = skuMapper.getSkuBySpuId(spuId);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<JSONUtil> editGoods(SpuDTO spuDTO) {
        final Date date = new Date();
        //spu
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //spuDetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailMapper.updateByPrimaryKeySelective(spuDetailEntity);

        //skus and stock
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuEntity.getId());
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        List<Long> skuId = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(skuId);
        stockMapper.deleteByIdList(skuId);
        this.saveSkusAndStock(spuDTO,spuEntity.getId(),date);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONUtil> deleteGoods(Integer spuId) {
        
        //spu
        spuMapper.deleteByPrimaryKey(spuId);

        //spuDetail
        spuDetailMapper.deleteByPrimaryKey(spuId);

        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        List<Long> skuId = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(skuId);
        stockMapper.deleteByIdList(skuId);

        return this.setResultSuccess();
    }
}
