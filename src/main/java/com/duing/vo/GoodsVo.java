package com.duing.vo;

import lombok.Data;

// vo = view object
// 为了区分 数据库的表结构 和 页面的展示数据结构  而出现
// vo可能随着需求的变化而变化  但是数据库的表结构不会轻易变化
@Data
public class GoodsVo {

    private String goodsId;
    private String goodsName;
    private String goodsType;
    private Double price;
    private String imgPath;
    private Double seckillPrice;
    private int stockNum;
}
