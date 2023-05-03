package com.hmdp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.controller.entity.SeckillVoucher;
import com.hmdp.controller.entity.VoucherOrder;
import com.hmdp.dto.Result;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * ClassName: VoucherOrderServiceImpl
 * Package: com.hmdp.service.impl
 * Description:
 *
 * @Author LTM
 * @Create 2023/4/27 14:23
 * @Version 1.0
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional
    public Result seckillVoucher(Long voucherId) {
        //1. 查询优惠券
        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        //2. 判断秒杀是否开始
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return Result.fail("秒杀尚未开始");
        }
        //3. 判断秒杀是否结束
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("秒杀已经结束");
        }
        //4. 判断库存是否充足
        if (voucher.getStock() < 1) {
            return Result.fail("券已经抢完！");
        }
        Long userId = UserHolder.getUser().getId();
        // 创建锁对象
        //SimpleRedisLock lock = new SimpleRedisLock("order" + userId, stringRedisTemplate);
        RLock lock = redissonClient.getLock("lock:order" + userId);
        // 获取锁
        boolean isLock = lock.tryLock();
        // 判断是否获取锁成功
        if (!isLock) {
            // 获取锁失败，返回错误提示
            return Result.fail("不允许重复下单/获取锁失败");
        }
        try {
            // 获取代理对象（事务）
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId);
        }finally {
            lock.unlock();
        }

    }

    @Transactional
    public Result createVoucherOrder(Long voucherId ) {
        //一人一单
        Long userId = UserHolder.getUser().getId();
        //查询订单id
        int count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        //判断是否存在
        if (count > 0) {
            return Result.fail("用户已购买过");
        }

        //5. 扣减库存
        boolean success = seckillVoucherService.update()
                .setSql("stock = stock -1 ")
                .eq("voucher_id", voucherId).gt("stock", 0)
                .update();
        if (!success) {
            return Result.fail("券已经抢完！");
        }

        //seckillVoucherService.lambdaUpdate()
        //        .set(SeckillVoucher::getStock, SeckillVoucher::getStock - 1)
        //        .eq(SeckillVoucher::getVoucherId, voucherId)
        //        .update();
        //6. 创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        //6.1 订单id
        Long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        //6.2 用户id

        voucherOrder.setUserId(userId);
        //6.3 代金券id
        voucherOrder.setVoucherId(voucherId);
        save(voucherOrder);
        //7. 返回订单id
        return Result.ok(orderId);
    }
}
