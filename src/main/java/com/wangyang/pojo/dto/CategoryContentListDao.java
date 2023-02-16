package com.wangyang.pojo.dto;

import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.pojo.vo.ContentVO;
import lombok.Data;

import java.util.List;
@Data
public class CategoryContentListDao {

    private String viewName;
    //    private Page<ArticleVO> page;
    private List<ContentVO> contents;
    private List<CategoryContentList> categoryContentLists;
    private CategoryVO category;
    private CategoryVO parentCategory;
    private List<CategoryVO> parentCategories;
    private List<CategoryVO> children;
    private List<CategoryVO> partner;
    private Category parent;
    /**
     * 第一个category文章列表的路径,
     * 相应的录路径格式在 CategoryServiceImpl#listCategoryVo()生成
     * 每一个CategoryVo包含linkPath：
     * @see com.wangyang.pojo.vo.CategoryVO#setLinkPath(String)
     * TemplateUtil.convertHtmlAndSave(categoryArticle, template.get());
     */
    private String path;
    /**
     * url路径的格式
     * <li class="page-item" th:each="item : ${#numbers.sequence(2, view.page.totalPages)}">
     *   <a class="page-link"  th:href="${view.linkPath}+'/'+${item}+'/page.html'" th:text="${item}">1</a>
     * </li>
     */
    private String linkPath;


    int totalPages ;
    int size ;
    long totalElements ;
}
