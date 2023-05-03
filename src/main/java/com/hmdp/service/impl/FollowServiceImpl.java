package com.hmdp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.controller.entity.Follow;
import com.hmdp.mapper.FollowMapper;
import com.hmdp.service.IFollowService;
import org.springframework.stereotype.Service;

/**
 * ClassName: FollowServiceImpl
 * Package: com.hmdp.service.impl
 * Description:
 *
 * @Author LTM
 * @Create 2023/4/27 14:22
 * @Version 1.0
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {
}
