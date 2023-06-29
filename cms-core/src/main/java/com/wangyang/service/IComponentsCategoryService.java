package com.wangyang.service;

import com.wangyang.pojo.entity.ComponentsCategory;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.service.base.ICrudService;

import java.util.List;
import java.util.Set;

public interface IComponentsCategoryService extends ICrudService<ComponentsCategory, ComponentsCategory, BaseVo,Integer> {
    List<ComponentsCategory> findByCategoryId(Integer categoryId);

    ComponentsCategory add(String viewName, int componentsId);
    void delete(int id);

    ComponentsCategory delete(Integer categoryId, Integer componentId);

    List<ComponentsCategory> findByCategoryId(Set<Integer> categoryIds);
}
