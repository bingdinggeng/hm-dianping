package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.controller.entity.VoucherOrder;
import com.hmdp.dto.Result;

/**
 * ClassName: IVoucherOrderService
 * Package: com.hmdp.service
 * Description:
 *
 * @Author LTM
 * @Create 2023/4/27 14:16
 * @Version 1.0
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {
    Result seckillVoucher(Long voucherId);

    Result createVoucherOrder(Long voucherId);
}
