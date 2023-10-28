package com.wangyang.service.base;

import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.dto.CategoryChild;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.ComponentsCategory;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.repository.ComponentsCategoryRepository;
import com.wangyang.repository.base.BaseCategoryRepository;
import com.wangyang.util.FormatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class AbstractBaseCategoryServiceImpl <CATEGORY extends BaseCategory,CATEGORYDTO extends BaseEntity,CATEGORYVO extends BaseVo>  extends AbstractCrudService<CATEGORY,CATEGORYDTO,CATEGORYVO,Integer>
        implements IBaseCategoryService<CATEGORY,CATEGORYDTO,CATEGORYVO>{
    @Autowired
    ComponentsCategoryRepository componentsCategoryRepository;
    BaseCategoryRepository<CATEGORY> baseCategoryRepository;
    public AbstractBaseCategoryServiceImpl(BaseCategoryRepository<CATEGORY> baseCategoryRepository) {
        super(baseCategoryRepository);
        this.baseCategoryRepository=baseCategoryRepository;
    }

    @Override
    public CategoryDto covertToDto(CATEGORY category) {
        CategoryDto categoryDto = new CategoryDto();
        BeanUtils.copyProperties(category,categoryDto);
        categoryDto.setLinkPath(FormatUtil.categoryListFormat(category));
        return categoryDto;
    }
    @Override
    public List<CATEGORY> listByIdsOrderComponent(Set<Integer> categoryIds){
        if(categoryIds.size()==0){
            return Collections.emptyList();
        }
        List<CATEGORY>  categories = baseCategoryRepository.findAll(new Specification<CATEGORY>() {
            @Override
            public Predicate toPredicate(Root<CATEGORY> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(root.get("id").in(categoryIds)).getRestriction();
            }
        }, Sort.by(Sort.Direction.DESC,"categoryInComponentOrder"));
        return categories;
    }
    @Override
    public List<CATEGORYVO> listByComponentsId(int componentsId){
        List<ComponentsCategory> componentsCategories = componentsCategoryRepository.findByComponentId(componentsId);
        Set<Integer> categoryIds = ServiceUtil.fetchProperty(componentsCategories, ComponentsCategory::getCategoryId);
        if(categoryIds.size()==0){
            return Collections.emptyList();
        }
//        List<Article> articles = articleRepository.findAllById(articleIds);
        List<CATEGORY>  categories = listByIdsOrderComponent(categoryIds);

        return convertToListVo(categories);
    }

    @Override
    public CATEGORY findByViewName(String viewName){
        return baseCategoryRepository.findByViewName(viewName);
    }

    @Override
    public List<CATEGORY> listByParentId(int i) {
        return baseCategoryRepository.findAll(new Specification<CATEGORY>() {
            @Override
            public Predicate toPredicate(Root<CATEGORY> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("parentId"),i)).getRestriction();
            }
        });
    }
}
