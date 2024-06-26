package com.lgy.dao;

import com.lgy.base.BaseMapper;
import com.lgy.vo.SaleChance;

public interface SaleChanceMapper extends BaseMapper<SaleChance, Integer> {
    /*
        由于考虑到多个模块均涉及多条件查询
        这⾥对于多条件分⻚查询⽅法由⽗接⼝BaseMapper定义
    */
}