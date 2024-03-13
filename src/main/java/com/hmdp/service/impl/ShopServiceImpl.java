package com.hmdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.CacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CacheClient cacheClient;
    @Override
    public Result queryById(Long id) {
        //解决缓存穿透(防止一直访问不存在的数据)
        //Shop shop = cacheClient.queryWithPassThrough(CACHE_SHOP_KEY , id, Shop.class , this::getById , CACHE_SHOP_TTL ,TimeUnit.MINUTES);
        //互斥锁解决缓存击穿(防止大家都去重建缓存了 确保一致性)   锁--给缓存重建过程加锁
        //Shop shop = cacheClient.queryWithMutex(CACHE_SHOP_KEY , id, Shop.class , this::getById , CACHE_SHOP_TTL ,TimeUnit.MINUTES);
        //逻辑过期解决缓存击穿(防止大家都去重建缓存了 确保可用性)
        Shop shop = cacheClient.queryWithLogicalExpire(CACHE_SHOP_KEY , id, Shop.class , this::getById , CACHE_SHOP_TTL ,TimeUnit.MINUTES);

        if (shop==null)
            return Result.fail("店铺不存在");

        return Result.ok(shop);
    }

    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if(id == null)
            return Result.fail("店铺id不能为空");
        //1.更新数据库
        updateById(shop);
        //2.删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        return Result.ok();
    }


}
