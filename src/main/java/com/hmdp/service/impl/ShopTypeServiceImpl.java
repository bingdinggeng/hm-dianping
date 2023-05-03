package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.controller.entity.ShopType;
import com.hmdp.dto.Result;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    public static final String CACHE_SHOP_TYPE_KEY = "cache:shop_type:";
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    //@Override
    //public Result typeList() {
    //    List<String> shopTypeListInRedis = stringRedisTemplate.opsForList().range(CACHE_SHOP_TYPE_KEY,0,-1);
    //    if (!shopTypeListInRedis.isEmpty()) {
    //        List<ShopType> shopTypeList = shopTypeListInRedis.stream().map(item ->{
    //            return JSONUtil.toBean(item,ShopType.class);
    //        }).collect(Collectors.toList());
    //        return Result.ok(shopTypeList);
    //    }
    //    List<ShopType> shopTypeList = query().orderByAsc("sort").list();
    //    List<String> redisList = shopTypeList.stream().map(item ->{
    //        return JSONUtil.toJsonStr(item);
    //    }).collect(Collectors.toList());
    //    stringRedisTemplate.opsForList().rightPushAll(CACHE_SHOP_TYPE_KEY,redisList);
    //
    //    return Result.ok(shopTypeList);
    //}

    @Override
    public Result typeList() {
        List<String> shopTypeListInRedis = stringRedisTemplate.opsForList().range(CACHE_SHOP_TYPE_KEY,0,-1);
        Long size = stringRedisTemplate.opsForList().size(CACHE_SHOP_TYPE_KEY);

        if(shopTypeListInRedis.size()!= 0){
            List<ShopType> shopTypeList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String stringRedis = shopTypeListInRedis.get(i);
                shopTypeList.add(JSONUtil.toBean(stringRedis,ShopType.class));
            }
            return Result.ok(shopTypeList);
        }

        List<ShopType> typeList = query().orderByAsc("sort").list();

        if(typeList.size() == 0){
            return Result.fail("没有shoptype数据");
        }
        for(ShopType shopType : typeList){
            stringRedisTemplate.opsForList().rightPush(CACHE_SHOP_TYPE_KEY,JSONUtil.toJsonStr(shopType));
        }
        return Result.ok(typeList);

       
    }
}
