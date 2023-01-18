package com.wangyang.pojo.vo;

import com.wangyang.pojo.entity.Tags;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

@Data
public class CategoryDetailVO {
    private String name;
    private String description;
    private Integer articleNumber;
    //    private Integer templateId;
    private String templateName;
    private Boolean haveHtml=true;

    private String viewName;
    //    @Column(columnDefinition = "bit(1) default true")
//    private Boolean status=true;
    private String picPath;
    private String picThumbPath;
    private String path;

    private Boolean recommend;

    private Boolean existNav;
    private String articleTemplateName;
    private String icon;
    // 每页显示文章的数量
    private Integer articleListSize;
    //    private Integer articleListPage=0;
    private Boolean isDesc;
    private List<Tags> tags;
}
