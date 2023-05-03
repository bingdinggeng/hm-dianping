package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.controller.entity.Shop;
import com.hmdp.dto.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IShopService extends IService<Shop> {

    Result queryById(Long id);

    Result update(Shop shop);
}
