package com.lgy.service;

import com.lgy.base.BaseService;
import com.lgy.dao.UserRoleMapper;
import com.lgy.vo.UserRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserRoleService extends BaseService<UserRole,Integer> {
    @Resource
    private UserRoleMapper userRoleMapper;
}
