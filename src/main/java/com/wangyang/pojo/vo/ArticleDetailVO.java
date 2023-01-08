package com.wangyang.pojo.vo;

import com.wangyang.pojo.dto.ArticleDto;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.Tags;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class ArticleDetailVO extends ArticleDto implements Serializable {
    private String originalContent;
    private String formatContent;
    private CategoryVO category;
    private Set<Integer> tagIds;
    private List<Tags> tags;
    private List<CategoryVO> parentCategory;

    //更新channel的文章视图名称,将html的更新从service转移controller
//    private Boolean isUpdateChannelFirstName=false;
    private Category oldCategory;

//    public Category getOldCategory() {
//        return oldCategory;
//    }
//
//    public void setOldCategory(Category oldCategory) {
//        this.oldCategory = oldCategory;
//    }
//
//    public Boolean getUpdateChannelFirstName() {
//        return isUpdateChannelFirstName;
//    }
////
//    public void setUpdateChannelFirstName(Boolean updateChannelFirstName) {
//        isUpdateChannelFirstName = updateChannelFirstName;
//    }
//
//    public String getOriginalContent() {
//        return originalContent;
//    }
//
//    public void setOriginalContent(String originalContent) {
//        this.originalContent = originalContent;
//    }
//
//    public String getFormatContent() {
//        return formatContent;
//    }
//
//    public void setFormatContent(String formatContent) {
//        this.formatContent = formatContent;
//    }
//
//    public Category getCategory() {
//        return category;
//    }
//
//    public void setCategory(Category category) {
//        this.category = category;
//    }
//
//    public Set<Integer> getTagIds() {
//        return tagIds;
//    }
//
//    public void setTagIds(Set<Integer> tagIds) {
//        this.tagIds = tagIds;
//    }
//
//    public List<Tags> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<Tags> tags) {
//        this.tags = tags;
//    }
}
