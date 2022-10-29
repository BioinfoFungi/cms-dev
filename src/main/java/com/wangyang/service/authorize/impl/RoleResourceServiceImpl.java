package com.wangyang.service.authorize.impl;


import com.wangyang.pojo.authorize.RoleResource;
import com.wangyang.pojo.enums.CrudType;
import com.wangyang.pojo.vo.BaseVo;
import com.wangyang.repository.authorize.ResourceRepository;
import com.wangyang.repository.authorize.RoleRepository;
import com.wangyang.repository.authorize.RoleResourceRepository;
import com.wangyang.service.authorize.IRoleResourceService;
import com.wangyang.service.base.AbstractCrudService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class RoleResourceServiceImpl extends AbstractCrudService<RoleResource,RoleResource, BaseVo,Integer>
        implements IRoleResourceService {



    private  final RoleResourceRepository roleResourceRepository;
    private final ApplicationContext applicationContext;
    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;
//    private final  IRoleService roleService;
//    private final IResourceService resourceService;
    public RoleResourceServiceImpl(RoleResourceRepository roleResourceRepository,
                                   ApplicationContext applicationContext,
                                   RoleRepository roleRepository,
                                   ResourceRepository resourceRepository){
        super(roleResourceRepository);
        this.roleResourceRepository =roleResourceRepository;
        this.applicationContext=applicationContext;
        this.roleRepository = roleRepository;
        this.resourceRepository = resourceRepository;
//        this.roleService =roleService;
//        this.resourceService = resourceService;

    }

    @Override
    public List<RoleResource> listAll() {
        return roleResourceRepository.findAll();
    }

    @Override
    public RoleResource save(RoleResource roleResource) {
        RoleResource resource = findBy(roleResource.getResourceId(), roleResource.getRoleId());
        if(resource==null){
            resource = super.save(roleResource);
            return resource;
        }
        return null;
    }

    @Override
    public RoleResource findBy(Integer resourceId, Integer roleId){
        List<RoleResource> roleResources = listAll().stream()
                .filter(roleResource -> roleResource.getResourceId().equals(resourceId) && roleResource.getRoleId().equals(roleId))
                .collect(Collectors.toList());
        return roleResources.size()==0?null:roleResources.get(0);
    }

    @Override
    public List<RoleResource> findByRoleId(int roleId){
        List<RoleResource> roleResources = listAll().stream()
                .filter(roleResource -> roleResource.getRoleId().equals(roleId))
                .collect(Collectors.toList());
        return roleResources;
    }

    @Override
    public List<RoleResource> findByResourceId(int resourceId){
        List<RoleResource> roleResources = listAll().stream()
                .filter(roleResource -> roleResource.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
        return roleResources;
    }

    @Override
    public List<RoleResource> findByResourceId(Set<Integer> ids) {
        List<RoleResource> roleResources = listAll().stream()
                .filter(roleResource -> ids.contains(roleResource.getResourceId()))
                .collect(Collectors.toList());
        return roleResources;
    }

    @Override
    public void init(){
//        RequestMappingHandlerMapping mapping = applicationContext.getBean("requestMappingHandlerMapping",RequestMappingHandlerMapping.class);
//        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();
//        List<RoleUrl> roleResourceName = new ArrayList<>();
//        for (RequestMappingInfo info : methodMap.keySet()){
//            HandlerMethod handlerMethod = methodMap.get(info);
//            Set<String> urlSet = info.getPatternsCondition().getPatterns();
//            String url = urlSet.iterator().next();
//            Set<RequestMethod> methodSet = info.getMethodsCondition().getMethods();
//            String methodName;
//            if(methodSet.size()!=0){
//                methodName=methodSet.iterator().next().name();
//            }else {
//                methodName="";
//            }
//            Method method = handlerMethod.getMethod();
//            if(method.isAnnotationPresent(Anonymous.class)){
//                Anonymous authorize = method.getAnnotation(Anonymous.class);
//                String roleName = authorize.role();
//                RoleUrl roleUrl = new RoleUrl(roleName,url,methodName);
//                roleResourceName.add(roleUrl);
//            }
//        }
//        List<Role> roleList = roleRepository.findAll();
//        List<Resource> resourceList = resourceRepository.findAll();
//        Map<String, Role> roleMap = ServiceUtil.convertToMap(roleList, Role::getEnName);
//        Map<String, Resource> resourceMap = resourceList.stream()
//                .collect(Collectors.toMap(resource -> resource.getMethod()+resource.getUrl(),resource -> resource));
//        roleResourceName.forEach(roleUrl->{
//            RoleResource roleResource = new RoleResource();
//            Role role = roleMap.get(roleUrl.getRole());
//            Resource resource = resourceMap.get(roleUrl.getMethod()+roleUrl.getUrl());
//            roleResource.setRoleId(role.getId());
//            roleResource.setResourceId(resource.getId());
//            roleResourceRepository.save(roleResource);
//        });
    }

    @Override
    public boolean supportType(CrudType type) {
        return false;
    }

    //    @Override
//    public List<RoleResourceDTO> findByRoleId(Integer id) {
//        List<RoleResource> roleResources = roleResourceService.findByRoleId(id);
//        Set<Integer> resourceIds = ServiceUtil.fetchProperty(roleResources, RoleResource::getResourceId);
//        List<Resource> resources = findByIds(resourceIds);
//        return resources;
//    }
}
