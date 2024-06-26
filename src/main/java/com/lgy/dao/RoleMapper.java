package com.lgy.dao;

import com.lgy.base.BaseMapper;
import com.lgy.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {
    // 查询⻆⾊列表
    public List<Map<String,Object>> queryAllRoles(Integer userId);

    //通过角色名查询角色记录
    public Role selectByRoleName(String roleName);

}