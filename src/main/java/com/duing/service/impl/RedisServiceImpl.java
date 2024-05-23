package com.duing.service.impl;

import com.duing.mapper.SeckillGoodsMapper;
import com.duing.model.SeckillGoods;
import com.duing.service.RedisService;
import com.duing.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public void initData(String goodsId, int stockNum) {
        // 存储数据时  设计好key的规则
        // 当前需求  根据商品id  找到唯一库存

        // 秒杀活动
        //   -》 在秒杀活动开始前  先由工作人员通过后台管理系统导入数据库中
        //   -》 再确保 redis中存储一份数据用于秒杀时刻使用   有额外同步数据的功能
        //         可以从指定的mysql表  取出需要的数据  初始化到redis中


        // 模拟通过请求存储数据
        // 自定义key的规则 【goods_id + "_count"】
        //   可以通过不同的商品id找到各自的库存数量
        //   <"111111_count",10>
        //   <"222222_count",20>
        //   <"333333_count",30>

        // 如果还要设置秒杀开始时间  如何设计key的规则？ 【goods_id + "_startTime"】
        redisUtil.set(goodsId + "_count", stockNum);


        // 商品  库存 名字 介绍  秒杀信息
        //   goods_id + "_goods"  {"name":"","desc":"",price:"",}

    }

    @Override
    public void initData() {
        List<SeckillGoods> seckillGoods = seckillGoodsMapper.getSeckillGoods();
        for (SeckillGoods goods : seckillGoods) {
            String goodsId = goods.getGoods_id();
            System.out.println("处理商品id为" + goodsId + "的详细信息");
            redisUtil.set(goodsId + "_count", goods.getStock_num());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = goods.getStart_time().toString();
            try {
                dateStr = format.format(goods.getStart_time());
            } catch (Exception e) {
            }
            System.out.println(dateStr);
            redisUtil.set(goodsId + "_startTime", dateStr);
        }
    }

    @Override
    public String seckill(String userId, String goodsId) {
//        return handle(userId, goodsId);
//        return handleByTrans(userId, goodsId);
        return handleByLua(userId, goodsId);
    }

    public String handle(String userId, String goodsId) {
        // 秒杀时刻的逻辑
        //    判断用户id有效性(在补充完登录系统后 自行补充)
        //    判断goodsId的有效性
        //       判断当前是否处于可秒杀的状态
        //           判断是否在商品的秒杀开始时间和结束时间的范围内
        //           判断是否有剩余库存
        //           判断用户的秒杀权限（是否秒杀成功过）
        //       减少库存
        //       新增订单

        //   先判断是否在秒杀开始时间之后  （还需要存储结束时间  判断是否在结束时间之前）
        String dateStr = (String) redisUtil.get(goodsId + "_startTime");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = null;
        try {
            startTime = format.parse(dateStr);
        } catch (Exception e) {
        }

        if (startTime == null || new Date().before(startTime)) {
            return "秒杀还未开始";
        }

        int stockNum = (int) redisUtil.get(goodsId + "_count");
        if (stockNum <= 0) {
            return "已被秒杀一空";
        }

        if (redisUtil.get(goodsId + "_" + userId) != null) {
            return "用户已秒杀成功过";
        }

        // 减库存  生成订单
        redisUtil.decr(goodsId + "_count");
        // 限额为1的情况   可以这样处理
        // 如果限额为2  获取value进行自增
        //    并且上面的判断是 get的值 > 限额
        redisUtil.set(goodsId + "_" + userId, 1);

        return userId + "用户秒杀成功";
    }

    // 使用事务
    public String handleByTrans(String userId, String goodsId) {
        String dateStr = (String) redisUtil.get(goodsId + "_startTime");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = null;
        try {
            startTime = format.parse(dateStr);
        } catch (Exception e) {
        }

        if (startTime == null || new Date().before(startTime)) {
            return "秒杀还未开始";
        }

        if (redisUtil.get(goodsId + "_" + userId) != null) {
            return "用户已秒杀成功过";
        }

        SessionCallback sessionCallback = new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations)
                    throws DataAccessException {
                // 编写事务的逻辑
                //  watch - multi - command - exec
                operations.watch(goodsId + "_count");

                int stockNum = (int) redisUtil.get(goodsId + "_count");
                if (stockNum <= 0) {
                    return "已被秒杀一空";
                }

                operations.multi();

                redisUtil.decr(goodsId + "_count");
                redisUtil.set(goodsId + "_" + userId, 1);

                return operations.exec();
            }
        };

        redisUtil.execute(sessionCallback);
        if (redisUtil.hasKey(goodsId + "_" + userId)) {
            return userId + "用户秒杀成功";
        }

        return userId + "用户秒杀失败";
    }

    // 使用lua脚本 （分布式锁）
    public String handleByLua(String userId, String goodsId) {
        String dateStr = (String) redisUtil.get(goodsId + "_startTime");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = null;
        try {
            startTime = format.parse(dateStr);
        } catch (Exception e) {
        }

        if (startTime == null || new Date().before(startTime)) {
            return "秒杀还未开始";
        }

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);

        script.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("lua/seckill.lua")));

        List<String> keyList = new ArrayList<>();
        keyList.add(userId);
        keyList.add(goodsId);

        Object result = redisUtil.execute(script, keyList);
        String reStr = String.valueOf(result);
        if ("0".equals(reStr)) {
            return "已被秒杀一空";
        } else if ("1".equals(reStr)) {
            return userId + "秒杀成功";
        } else if ("2".equals(reStr)) {
            return userId + "已经秒杀过";
        } else {
            return "秒杀异常";
        }
    }


}
