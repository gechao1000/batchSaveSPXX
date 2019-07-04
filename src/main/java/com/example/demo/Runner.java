package com.example.demo;

import com.example.demo.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class Runner implements CommandLineRunner {

    @Resource
    RedisService redisService;

    @Override
    public void run(String... args) throws Exception {
        if (args != null && args.length > 0 && "start".equals(args[0])) {
            log.info("-------------START SAVE-------------");
            redisService.batchSaveSPXX();
            redisService.batchSaveSPKCZT();
        }
    }
}
