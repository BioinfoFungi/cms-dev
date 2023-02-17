package com.wangyang.pojo.entity;

import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import javax.persistence.*;
@Entity
@DiscriminatorValue(value = "0")
@Data
//@Table(name = "t_category")
public class Category extends BaseCategory{

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;

//    @Column(columnDefinition = "int default 0")
//    private Integer parentId;
    @Column(columnDefinition = "longtext")
    private String description;
    @Column(name = "original_content", columnDefinition = "longtext")
    private String originalContent;
    @Column(name = "format_content", columnDefinition = "longtext")
    private String formatContent;
    @Column(columnDefinition = "int default 0")
    private Integer articleNumber;
//    private Integer templateId;
    private String templateName;
    @Column(columnDefinition = "bit(1) default true")
    private Boolean haveHtml=true;

    private String viewName;
//    @Column(columnDefinition = "bit(1) default true")
//    private Boolean status=true;
    private String picPath;
    private String picThumbPath;
    private String path;
//    @Column(name = "category_order",columnDefinition = "int default 1")
//    private Integer order;
    @Column(columnDefinition = "bit(1) default false")
    private Boolean recommend=false;
    @Column(columnDefinition = "bit(1) default false")
    private Boolean existNav=false;
    private String articleTemplateName;
    private String recommendTemplateName;
    private String icon;
    // 每页显示文章的数量
    private Integer articleListSize=10;
//    private Integer articleListPage=0;
    private Boolean isDesc=true;
    private Integer categoryInComponentOrder=0;
    private Boolean useHtml=true;
    public Boolean getDesc() {
        return isDesc;
    }

    public void setDesc(Boolean desc) {
        isDesc = desc;
    }
}
