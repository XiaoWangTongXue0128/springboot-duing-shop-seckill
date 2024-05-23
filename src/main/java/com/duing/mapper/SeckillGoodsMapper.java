package com.duing.mapper;

import com.duing.model.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SeckillGoodsMapper {

    SeckillGoods getSecGoodsById(String goodsId);

    List<SeckillGoods>  getSeckillGoods();

    void reduceStockNum(SeckillGoods seckillGoods);
}
