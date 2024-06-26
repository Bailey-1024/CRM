package com.lgy.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lgy.base.BaseService;
import com.lgy.dao.CusDevPlanMapper;
import com.lgy.dao.SaleChanceMapper;
import com.lgy.query.CusDevPlanQuery;
import com.lgy.utils.AssertUtil;
import com.lgy.vo.CusDevPlan;
import com.lgy.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan, Integer> {
    @Resource
    private CusDevPlanMapper cusDevPlanMapper;
    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件查询计划项列表
     *
     * @param cusDevPlanQuery
     * @return
     */
    public Map<String, Object> queryCusDevPlansByParams(CusDevPlanQuery cusDevPlanQuery) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(cusDevPlanQuery.getPage(), cusDevPlanQuery.getLimit());
        PageInfo<CusDevPlan> pageInfo = new PageInfo<>(selectByParams(cusDevPlanQuery));
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }


    /**
     * 添加客户开发计划项数据
     * 1. 参数校验
     * 营销机会ID ⾮空 记录必须存在
     * 计划项内容 ⾮空
     * 计划项时间 ⾮空
     * 2. 设置参数默认值
     * is_valid     默认有效
     * crateDate    系统当前时间
     * updateDate   系统当前时间
     * 3. 执⾏添加，判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCusDevPlan(CusDevPlan cusDevPlan) {
        //1. 参数校验
        checkCusDevPlanParams(cusDevPlan);
        //2. 设置参数默认值
        //      is_valid     默认有效
        cusDevPlan.setIsValid(1);
        //      crateDate    系统当前时间
        cusDevPlan.setCreateDate(new Date());
        //      updateDate   系统当前时间
        cusDevPlan.setUpdateDate(new Date());
        //3. 执⾏添加，判断结果
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan) != 1, "计划添加失败");
    }


    /**
     * 更新客户开发计划项数据
     * 1. 参数校验
     *      计划项ID   非空 数据存在
     *      营销机会ID ⾮空 记录必须存在
     *      计划项内容 ⾮空
     *      计划项时间 ⾮空
     * 2. 设置参数默认值
     *      修改时间 系统当前时间
     * 3. 执⾏添加，判断结果
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan){
        //1. 参数校验
        AssertUtil.isTrue(cusDevPlan.getId()==null,"数据异常");
        checkCusDevPlanParams(cusDevPlan);
        //2. 设置参数默认值
        cusDevPlan.setUpdateDate(new Date());
        //3. 执⾏更新，判断结果
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) != 1, "计划更新失败");
    }


    /**
     * 删除客户开发计划项数据
     *  1、判断ID是否为空，且数据存在
     *  2、修改isValid属性
     *  3、执行更新操作
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCusDevPlan(Integer id){
        //1、判断ID是否为空，且数据存在
        AssertUtil.isTrue(id==null,"待删除的记录不存在");
        //2、修改isValid属性
        //通过id查询计划项对象
        CusDevPlan cusDevPlan = cusDevPlanMapper.selectByPrimaryKey(id);
        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());
        //3、执行更新操作
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"删除失败");

    }




    /**
     *  1. 参数校验
     *          营销机会ID ⾮空 记录必须存在
     *          计划项内容 ⾮空
     *          计划项时间 ⾮空
     * @param cusDevPlan
     */
    private void checkCusDevPlanParams(CusDevPlan cusDevPlan) {
        //营销机会ID ⾮空 记录必须存在
        Integer sId=cusDevPlan.getSaleChanceId();
        AssertUtil.isTrue(sId==null || saleChanceMapper.selectByPrimaryKey(sId)==null,"数据异常");
        //计划项内容 ⾮空
        AssertUtil.isTrue(StringUtils.isBlank(cusDevPlan.getPlanItem()),"计划项内容不能为空");
        //计划项时间 ⾮空
        AssertUtil.isTrue(cusDevPlan.getPlanDate()==null,"计划时间不能为空");
    }


}
