package com.duing.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

//秒杀商品表
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillGoods {

    private long id;
    private String goods_id;
    private double seckill_price;
    private int stock_num;
    private Date start_time;
    private Date end_time;
}
