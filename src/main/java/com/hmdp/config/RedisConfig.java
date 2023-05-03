package com.hmdp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: RedisConfig
 * Package: com.hmdp.utils
 * Description:
 *
 * @Author LTM
 * @Create 2023/5/2 9:38
 * @Version 1.0
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedissonClient redissonClient(){
        //配置类
        Config config = new Config();
        //添加redis地址 这里添加了单点的地址， 也可以使用config.useclusterServers()添加集群地址
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        //创建客户端
        return Redisson.create(config);
    }
}
