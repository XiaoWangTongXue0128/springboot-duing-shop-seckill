package com.duing.service;

public interface RedisService {

    void initData(String goodsId, int stockNum);

    void initData();

    String seckill(String userId, String goodsId);
}
