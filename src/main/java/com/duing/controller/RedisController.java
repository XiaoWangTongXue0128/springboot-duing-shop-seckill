package com.duing.controller;

import com.duing.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

//    @GetMapping("/initData")
//    public String initData(String goodsId, int stockNum) {
//        redisService.initData(goodsId, stockNum);
//        return "success";
//    }

    @GetMapping("/initData")
    public String initData() {
        redisService.initData();
        return "success";
    }


    @GetMapping("/seckillAPI")
    public String seckill(String userId, String goodsId) {
        if (userId == null || goodsId == null) {
            return "参数异常";
        }
        System.out.println(userId + "——" + goodsId);
        String result = redisService.seckill(userId, goodsId);
        return result;
    }
}
