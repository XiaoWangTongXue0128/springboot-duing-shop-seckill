package com.duing.service.impl;

import com.duing.mapper.GoodsMapper;
import com.duing.mapper.SeckillGoodsMapper;
import com.duing.model.Goods;
import com.duing.model.SeckillGoods;
import com.duing.service.GoodsService;
import com.duing.vo.GoodsDetailVo;
import com.duing.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public List<GoodsVo> getGoods() {
        // 通过多表关联  一次查出来
        // 每个表单独查询
        // 从第一个表中查多个数据  再根据每行数据从第二个表中查询
        List<Goods> goodsList = goodsMapper.getGoods();
        List<GoodsVo> result = new ArrayList<>();
        for (Goods goods : goodsList) {

            SeckillGoods seckillGoods = seckillGoodsMapper
                    .getSecGoodsById(goods.getGoods_id());

            GoodsVo vo = new GoodsVo();
            vo.setGoodsId(goods.getGoods_id());
            vo.setGoodsName(goods.getGoods_name());
            vo.setGoodsType(goods.getGoods_type());
            vo.setPrice(goods.getPrice());
            vo.setImgPath(goods.getImg_path());

            vo.setSeckillPrice(seckillGoods.getSeckill_price());
            vo.setStockNum(seckillGoods.getStock_num());
            result.add(vo);
        }
        return result;
    }


    @Override
    public GoodsDetailVo getGoodsDetail(String goodsId) {
        Goods goods = goodsMapper.getGoodsById(goodsId);
        SeckillGoods seckillGoods = seckillGoodsMapper.getSecGoodsById(goodsId);

        GoodsDetailVo detailVo = new GoodsDetailVo();
        detailVo.setName(goods.getGoods_name());
        detailVo.setGoodsId(goods.getGoods_id());
        detailVo.setImgPath(goods.getImg_path());
        detailVo.setPrice(goods.getPrice());

        detailVo.setSeckillPrice(seckillGoods.getSeckill_price());
        detailVo.setStockNum(seckillGoods.getStock_num());
        detailVo.setStartTime(seckillGoods.getStart_time());
        detailVo.setEndTime(seckillGoods.getEnd_time());

        return detailVo;
    }

    @Override
    public List<GoodsVo> selectGoods() {
        List<Goods> goodsList = goodsMapper.selectGoods();
        List<GoodsVo> result = new ArrayList<>();
        for (Goods goods : goodsList) {

            GoodsVo vo = new GoodsVo();
            vo.setGoodsId(goods.getGoods_id());
            vo.setGoodsName(goods.getGoods_name());
            vo.setGoodsType(goods.getGoods_type());
            vo.setPrice(goods.getPrice());
            vo.setImgPath(goods.getImg_path());

            vo.setSeckillPrice(goods.getSeckillGoods().getSeckill_price());
            vo.setStockNum(goods.getSeckillGoods().getStock_num());
            result.add(vo);
        }
        return result;
    }


    @Override
    public void reduceStockNum(String goodsId) {
        SeckillGoods seckillGoods = seckillGoodsMapper.getSecGoodsById(goodsId);
        seckillGoods.setStock_num(seckillGoods.getStock_num() - 1);
        seckillGoodsMapper.reduceStockNum(seckillGoods);
    }
}
