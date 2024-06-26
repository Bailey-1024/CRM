package com.lgy.dao;

import com.lgy.base.BaseMapper;
import com.lgy.vo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User, Integer> {

    //根据用户名查找对象
    User queryUserByUserName(String userName);

    //查询所有的销售人员
    public List<Map<String,Object>> queryAllSales();


}