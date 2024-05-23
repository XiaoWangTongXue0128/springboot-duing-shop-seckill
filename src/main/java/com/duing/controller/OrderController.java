package com.duing.controller;

import com.duing.model.Order;
import com.duing.service.GoodsService;
import com.duing.service.OrderService;
import com.duing.service.RedisService;
import com.duing.vo.GoodsDetailVo;
import com.duing.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
public class OrderController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;


    @PostMapping("/toSeckill")
    public String toSeckill(Model model, String goodsId) {
        System.out.println("请求的商品id为：" + goodsId);
        // 如果有用户信息  先要判断用户信息的有效性
        GoodsDetailVo vo = goodsService.getGoodsDetail(goodsId);

        // 通过redis处理秒杀是否成功
        // 秒杀成功后在mysql存储数据
        String result = redisService.seckill("110", goodsId);
        if (!result.contains("秒杀成功")) {
            model.addAttribute("msg", result);
            return "fail";
        }

        // 减库存  生成订单
        goodsService.reduceStockNum(goodsId);

        Order order = new Order();
        order.setOrder_id(new Date().getTime() + "_110");
        order.setUser_id("110");
        order.setGoods_id(goodsId);
        order.setTelephone("400-678-6077");
        order.setAddress("黑龙江哈尔滨南岗区金爵万象");
        orderService.addOrder(order);

        OrderVo orderVo = new OrderVo();
        orderVo.setGoodsName(vo.getName());
        orderVo.setImgPath(vo.getImgPath());
        orderVo.setPrice(vo.getSeckillPrice());
        orderVo.setCreateTime(new Date());
        orderVo.setTelephone(order.getTelephone());
        orderVo.setAddress(order.getAddress());
        orderVo.setStatus(0);

        model.addAttribute("orderVo", orderVo);
        return "order";
    }
}
