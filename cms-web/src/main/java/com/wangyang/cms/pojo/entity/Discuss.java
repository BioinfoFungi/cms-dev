package com.wangyang.cms.pojo.entity;

import com.wangyang.cms.pojo.entity.base.BaseDiscuss;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * 问答的数据结构
 */
@Entity
@DiscriminatorValue(value = "0")
public class Discuss extends BaseDiscuss {

}
