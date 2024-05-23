package com.duing.controller;

import com.duing.service.GoodsService;
import com.duing.vo.GoodsDetailVo;
import com.duing.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;

@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/")
    public String list(Model model) {
        // 逐层调用  controller - service - dao - xml
//        List<GoodsVo> result = goodsService.getGoods();
        List<GoodsVo> result = goodsService.selectGoods();
        model.addAttribute("goodsList", result);
        return "list";
    }

    @GetMapping("/goodsDetail/{goodsId}")
    public String goodsDetail(Model model,
                              @PathVariable String goodsId) {
        GoodsDetailVo detailVo = goodsService.getGoodsDetail(goodsId);
        model.addAttribute("detailVo", detailVo);

        Date startTime = detailVo.getStartTime();
        Date endTime = detailVo.getEndTime();
        Date nowTime = new Date();

        // 判断秒杀的状态
        int status;
        // 未开始状态下  倒计时的秒数
        int remainSeconds = -1;
        if (nowTime.before(startTime)) {
            // 秒杀未开始
            status = 0;
            remainSeconds = (int) ((startTime.getTime() - nowTime.getTime()) / 1000);
        } else if (nowTime.after(endTime)) {
            // 秒杀已结束
            status = 2;
        } else {
            // 秒杀进行中
            status = 1;
        }

        model.addAttribute("status", status);
        model.addAttribute("remainSeconds", remainSeconds);
        return "detail";
    }
}
