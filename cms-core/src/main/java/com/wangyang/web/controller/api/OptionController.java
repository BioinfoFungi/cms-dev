package com.wangyang.web.controller.api;

import com.wangyang.common.CmsConst;
import com.wangyang.common.enums.Lang;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ImageUtils;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.enums.TemplateData;
import com.wangyang.pojo.enums.TemplateType;
import com.wangyang.pojo.vo.ArticleDetailVO;
import com.wangyang.pojo.vo.BaseCategoryVo;
import com.wangyang.pojo.vo.ContentDetailVO;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.repository.CategoryTemplateRepository;
import com.wangyang.service.*;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.service.base.IBaseCategoryService;
import com.wangyang.service.templates.IBaseTemplateService;
import com.wangyang.service.base.IContentService;
import com.wangyang.service.templates.IComponentsService;
import com.wangyang.service.templates.ITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/option")
public class OptionController {

    @Autowired
    IOptionService optionService;

    @Autowired
    IArticleService articleService;

    @Autowired
    IComponentsService componentsService;

    @Autowired
    IHtmlService htmlService;

    @Autowired
    ICategoryService categoryService;

    @Autowired
    ITemplateService templateService;


    @Autowired
    ISheetService sheetService;


    @Autowired
    IContentService<Content, ContentDetailVO, ContentVO> contentService;

    @Autowired
    IBaseCategoryService<BaseCategory,BaseCategory,BaseCategoryVo> baseCategoryService;


    @Autowired
    IBaseTemplateService<BaseTemplate,BaseTemplate, BaseVo> baseTemplateService;


    @Autowired
    CategoryTemplateRepository categoryTemplateRepository;


    @Autowired
    IUserArticleService userArticleService;
    @Autowired
    IUserService userService;

    @PostMapping
    public List<Option> addOption(@RequestBody List<Option> options){
        return optionService.saveUpdateOptionList(options);
    }
    @GetMapping
    public List<Option> list(){
        return optionService.list();
    }
    @GetMapping("/updateLanguage")
    public String updateLanguage(){

        List<Content> contents = contentService.listAll();
        contents.forEach(content -> {
            if(content.getLang()==null){
                content.setLang(Lang.ZH);
                contentService.save(content);
            }
        });

        List<BaseCategory> baseCategories = baseCategoryService.listAll();
        baseCategories.forEach(baseCategory -> {
            if(baseCategory.getLang()==null){
                baseCategory.setLang(Lang.ZH);
                baseCategoryService.save(baseCategory);
            }
            if(baseCategory.getTemplateData()==null){
                baseCategory.setTemplateData(TemplateData.OTHER);
                baseCategoryService.save(baseCategory);
            }



        });

        List<BaseTemplate> baseTemplates = baseTemplateService.listAll();
        baseTemplates.forEach(baseTemplate -> {
            if(baseTemplate.getLang()==null){
                baseTemplate.setLang(Lang.ZH);
                baseTemplateService.save(baseTemplate);
            }
        });

        List<Category> categories = categoryService.listAll();
        categories.forEach(item->{
            if(item.getTemplateName()!=null){
                Template template = templateService.findByEnName(item.getTemplateName());
                CategoryTemplate findCategoryTemplate = categoryTemplateRepository.findByCategoryIdAndTemplateId(item.getId(), template.getId());
                if(findCategoryTemplate==null){
                    CategoryTemplate categoryTemplate = new CategoryTemplate(item.getId(),template.getId(),template.getTemplateType());
                    categoryTemplateRepository.save(categoryTemplate);
                }
                List<Template> templates = templateService.findByChild(template.getId());
                templates.forEach(templateChild->{
                    CategoryTemplate findCategoryTemplate2 = categoryTemplateRepository.findByCategoryIdAndTemplateId(item.getId(), templateChild.getId());
                    if(findCategoryTemplate2==null){
                        CategoryTemplate categoryTemplate = new CategoryTemplate(item.getId(),templateChild.getId(),templateChild.getTemplateType());
                        categoryTemplateRepository.save(categoryTemplate);
                    }
                });
            }

        });


        List<Article> articles = articleService.listAll();
        for (Article article : articles) {
            List<UserArticle> userArticles = userArticleService.listByArticleId(article.getId());
            if(userArticles.size()==0){
                User user = userService.findUserByUsername("admin");
                UserArticle userArticle = new UserArticle(user.getId(),article.getId());
                userArticleService.save(userArticle);
            }
        }


        return "success!";
    }
//    @GetMapping("/updateTemplateType")
//    public void updateTemplateType(){
//        List<BaseTemplate> baseTemplates = baseTemplateService.listAll();
//        TemplateType[] values = TemplateType.values();
//        TemplateData[] templateDatas = TemplateData.values();
//        for (BaseTemplate baseTemplate :baseTemplates){
//           if(baseTemplate instanceof  Template){
//               Template template = (Template) baseTemplate;
//               if(template.getTemplateType()==null && template.getTemplateTypeTmp()!=null){
//                   TemplateType templateType = values[template.getTemplateTypeTmp()];
//                   template.setTemplateType(templateType);
//                   baseTemplateService.save(template);
//               }
//               if(template.getTemplateData()==null &&template.getTemplateDateTmp()!=null){
//                   TemplateData templateData = templateDatas[template.getTemplateDateTmp()];
//                   template.setTemplateData(templateData);
//                   baseTemplateService.save(template);
//               }
//
//
//           }
//        }
//
//    }
    @GetMapping("/initialize")
    public String initialize(){
        // 初始化组件
        List<Components> components = componentsService.listAll();
        components.forEach(component -> {
            Object o = componentsService.getModel(component);
            TemplateUtil.convertHtmlAndSave(o, component);
        });

        List<Category> categories = categoryService.listAll();
        categories.forEach(category -> {
            if(true){
//                if(category.getTemplateName()==null){
//                    category.setTemplateName(CmsConst.DEFAULT_CATEGORY_TEMPLATE);
//                }
                if(category.getArticleTemplateName()==null){
                    category.setArticleTemplateName(CmsConst.DEFAULT_ARTICLE_TEMPLATE);
                }
                if(category.getDesc()==null){
                    category.setDesc(true);
                }
                if(category.getArticleListSize()==null){
                    category.setArticleListSize(10);
                }
                category.setPath(CMSUtils.getCategoryPath());
                categoryService.save(category);
            }
            htmlService.articleTopListByCategoryId(category.getId());
            htmlService.convertArticleListBy(category);
        });

        List<Article> articles = articleService.listHaveHtml();
        articles.forEach(article->{
            if(true){
                article = articleService.createOrUpdate(article);
                articleService.generateSummary(article);
                if(article.getLikes()==null){
                    article.setLikes(0);
                }
                if(article.getVisits()==null){
                    article.setVisits(0);
                }
                if(article.getCommentNum()==null){
                    article.setCommentNum(0);
                }
                if(article.getOpenComment()==null){
                    article.setOpenComment(false);
                }
                if(article.getPicPath()==null){
                    article.setPicPath(ImageUtils.getImgSrc(article.getFormatContent()));
                }
                if(article.getParentId()==null){
                    article.setParentId(0);
                }
                if(article.getOrder()==null){
                    article.setOrder(0);
                }
                if(article.getDirection()==null){
                    article.setDirection("right");
                }
                if(article.getTop()==null){
                    article.setTop(false);
                }
                Category category = categoryService.findById(article.getCategoryId());
                article.setPath(CMSUtils.getArticlePath());
                article.setTemplateName(category.getArticleTemplateName());

                articleService.save(article);
//                log.info("更新["+article.getTitle()+"]内容!!!");
            }
            ArticleDetailVO articleDetailVO = articleService.convert(article);
            htmlService.conventHtml(articleDetailVO);
        });

        List<Sheet> sheets = sheetService.listAll();
        sheets.forEach(sheet -> {
            htmlService.convertArticleListBy(sheet);
        });
        return "success!!";
    }


}
