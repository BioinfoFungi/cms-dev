package com.wangyang.pojo.entity.shop;

import com.wangyang.pojo.entity.base.Content;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "3")
@Data
public class Goods extends Content {
    private Integer userId;
    @Column(name = "cost_")
    private Double cost;
    private String goodsImg;
    private String goodsQr;
    private String costUrl;
}
