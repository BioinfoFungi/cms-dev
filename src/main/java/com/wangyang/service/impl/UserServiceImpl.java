package com.wangyang.service.impl;


import com.wangyang.common.exception.UserException;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.authorize.*;
import com.wangyang.pojo.dto.UserDto;
import com.wangyang.pojo.entity.Attachment;
import com.wangyang.pojo.enums.FileWriteType;
import com.wangyang.repository.UserRepository;
import com.wangyang.service.IRoleService;
import com.wangyang.service.IUserRoleService;
import com.wangyang.service.IUserService;
import com.wangyang.service.base.BaseAuthorizeServiceImpl;
import com.wangyang.service.service.IAttachmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wangyang
 * @date 2021/5/5
 */
@Service
@Transactional
public class UserServiceImpl extends BaseAuthorizeServiceImpl<User>
        implements IUserService {

    private final UserRepository userRepository;
    private final IUserRoleService userRoleService;
    private final IRoleService roleService;
    private final IAttachmentService attachmentService;

    public UserServiceImpl(UserRepository userRepository,
                           IUserRoleService userRoleService,
                           IRoleService roleService,
                           IAttachmentService attachmentService) {
        super(userRepository);
        this.userRepository=userRepository;
        this.userRoleService=userRoleService;
        this.roleService=roleService;
        this.attachmentService = attachmentService;
    }
    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }
    @Override
    public User addUser(UserParam userParam) {
        User user = new User();
        BeanUtils.copyProperties(userParam,user);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(int id, UserParam userParam) {
        User user = findUserById(id);
        BeanUtils.copyProperties(userParam,user);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(int id, UserParam userParam, MultipartFile file) {
        User user = findUserById(id);
        BeanUtils.copyProperties(userParam,user,"password","avatar");
        if(userParam.getPassword()!=null||!"".equals(userParam.getPassword())){
            user.setPassword(userParam.getPassword());
        }

        if(file!=null){
            Attachment attachment = attachmentService.upload(file, user.getUsername(), FileWriteType.COVER,attachmentService.getAttachmentType());
            user.setAvatar(attachment.getPath());
        }
        return userRepository.save(user);
    }

    @Override
    public User findUserById(int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(!userOptional.isPresent()){
            throw new UserException("需要操作的用户不存在!!");
        }
        User user = userOptional.get();
        return  user;
    }

    @Override
    public List<User> findAllById(Collection<Integer> ids) {
        return userRepository.findAllById(ids);
    }


    @Override
    public List<UserDto> listAllUserDto() {
        List<UserDto> userDtos = userRepository.findAll().stream().map(user -> {
                    UserDto userDto = new UserDto();
                    BeanUtils.copyProperties(user,userDto);
                    return userDto;
                }
        ).collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public User findById(Integer Id) {
        User user = new User();
        BeanUtils.copyProperties(super.findById(Id),user);
        user.setPassword(null);
        return user;
    }

    @Override
    public User delUser(int id) {
        User user = findById(id);
        if(user.getUsername().equals("admin")){
            throw new UserException("超级管理员不能删除！");
        }
        List<UserRole> userRoles = userRoleService.findByUserId(user.getId());
        userRoleService.deleteAll(userRoles);
        userRepository.delete(user);
        return user;
    }




    @Override
    public Page<User> pageUser(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> {
          User user1 = new User();
          BeanUtils.copyProperties(user,user1);
          user1.setPassword("");
          return user1;
        });
    }

    @Override
    public UserDetailDTO login(String username, String password) {
        UserDetailDTO userDetailDTO = new UserDetailDTO();
        User user = findUserByUsername(username);
        if(user==null){
            throw new UserException("用户名不存在！");
        }
        if(!user.getPassword().equals(password)){
            throw new UserException("用户名或密码错误！");
        }

        List<Role> roles = roleService.listAll();
        List<UserRole> userRoles = userRoleService.listAll();
        userRoles = userRoles.stream()
                .filter(userRole -> userRole.getUserId().equals(user.getId()))
                .collect(Collectors.toList());
        Set<Integer> roleIds = ServiceUtil.fetchProperty(userRoles, UserRole::getRoleId);
        Set<Role> usrRoles = roles.stream()
                .filter(role -> roleIds.contains(role.getId()))
                .collect(Collectors.toSet());
        userDetailDTO.setRoles(usrRoles);
        BeanUtils.copyProperties(user,userDetailDTO);
        return userDetailDTO;
    }

    @Override
    public User findUserByUsername(String username) {
        List<User> users = userRepository.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("username"), username)).getRestriction();
            }
        });
        return CollectionUtils.isEmpty(users)?null:users.get(0);
    }

    @Override
    public UserDto findUserDaoById(int userId) {
        User user = findById(userId);
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        List<Role> roles = roleService.findByUser(user.getId());
        userDto.setRoles(roles);
        return userDto;
    }
}
