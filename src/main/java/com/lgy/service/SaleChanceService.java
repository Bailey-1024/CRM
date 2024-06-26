package com.lgy.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lgy.base.BaseService;
import com.lgy.dao.SaleChanceMapper;
import com.lgy.enums.DevResult;
import com.lgy.enums.StateStatus;
import com.lgy.query.SaleChanceQuery;
import com.lgy.utils.AssertUtil;
import com.lgy.utils.PhoneUtil;
import com.lgy.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件分页查询营销机会（BaseService 中有对应的方法）
     *
     * @param query
     * @return
     */
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery query) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(), query.getLimit());
        PageInfo<SaleChance> pageInfo = new PageInfo<>(saleChanceMapper.selectByParams(query));
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 营销机会数据添加
     * 1.参数校验
     * customerName客户名称:⾮空
     * linkMan联系人:⾮空
     * linkPhone联系号码:⾮空 11位⼿机号
     * 2.设置相关参数默认值
     * createMan创建人：当前登录用户名
     * assignMan指派人
     * 如果为设置指派人（默认）
     * state分配状态（0=未分配，1=已分配）
     * 0=未分配
     * assignTime指派时间
     * 设置为null
     * devResult开发状态（0=未开发，1=开发中，2=开发成功，3=开发失败）
     * 0=未开发（默认）
     * 如果设置了指派人
     * state分配状态（0=未分配，1=已分配）
     * 1=已分配
     * assignTime指派时间
     * 系统当前时间
     * devResult开发状态（0=未开发，1=开发中，2=开发成功，3=开发失败）
     * 1=开发中
     * isValid:默认有效数据(1-有效 0-⽆效)
     * 设置为有效 1=有效
     * createDate updateDate:
     * 默认当前系统时间
     * updateDate
     * 默认是系统当前时间
     * 3.执⾏添加 判断受影响的行数
     *
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance) {
        // 1.参数校验
        checkSaleChanceParams(saleChance.getCustomerName(), saleChance.getLinkMan(),
                saleChance.getLinkPhone());
        //2、设置相关字段的默认值
        //isValid是否有效(1=有效 0=⽆效) 设置为有效 1=有效
        saleChance.setIsValid(1);
        //createDate创建时间 默认是系统当前时间
        saleChance.setCreateDate(new Date());
        //updateDate 更新时间 默认是系统当前时间
        saleChance.setUpdateDate(new Date());
        //判断是否设置了指派人
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            //如果为空，则表示未设置指派人
            //state分配状态 （0=未分配， 1=已分配） 0=未分配
            saleChance.setState(StateStatus.UNSTATE.getType());
            //assignTime指派时间 设置为null
            saleChance.setAssignTime(null);
            //devResult开发状态（0=未开发，1=开发中，2=开发成功，3=开发失败） 0=未开发（默认）
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        } else {
            //如果不为空，则表示设置了指派人
            //state分配状态（0=未分配，1=已分配）1=已分配
            saleChance.setState(StateStatus.STATED.getType());
            //assignTime指派时间 :系统当前时间
            saleChance.setAssignTime(new Date());
            //devResult开发状态（0=未开发，1=开发中，2=开发成功，3=开发失败） 1=开发中
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }

        // 3.执⾏添加 判断受影响的行数
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance) != 1, "添加营销机会失败");
    }

    /**
     * 更新营销机会
     * 1、参数校验
     * 营销机会ID 非空 数据库中对应的记录存在
     * customerName客户名称:⾮空
     * linkMan联系人:⾮空
     * linkPhone联系号码:⾮空 11位⼿机号
     * <p>
     * 2、设置相关参数的默认值
     * updateDate更新时间  设置为系统当前时间
     * assignMan指派人
     * 原始数据未设置
     * 修改后未设置
     * 不需要操作
     * 修改后已设置
     * assignTime指派时间   设置为系统当前时间
     * 分配状态    1=已分配
     * 开发状态    1=开发中
     * 原始数据已设置
     * 修改后未设置
     * assignTime指派时间  设置为null
     * 分配状态    0=未分配
     * 开发状态    0=未开发
     * 修改后已设置
     * 判断修改前后是否是同一个指派人
     * 如果是，则不需要操作
     * 如果不是，则需要更新  assignTime指派时间 设置为系统当前时间
     * 3、执行更新操作，判断受影响的行数
     *
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance) {
        /*1、参数校验*/
        //营销机会ID 非空 数据库中对应的记录存在
        AssertUtil.isTrue(saleChance.getId() == null, "待更新记录不存在");
        //通过主键查询对象
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        //判断数据库中对应的记录存在
        AssertUtil.isTrue(temp == null, "待更新记录不存在");
        //参数校验
        checkSaleChanceParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());

        /*2、设置相关参数的默认值*/
        //updateDate更新时间 设置为系统当前时间
        saleChance.setUpdateDate(new Date());
        //assignMan指派人
        //判断原始数据是否存在
        if (StringUtils.isBlank(temp.getAssignMan())) {//不存在
            //判断修改后的值是否存在
            if (!StringUtils.isBlank(saleChance.getAssignMan())) {//修改前为空，修改后有值
                //assignTime指派时间 设置为系统当前时间
                saleChance.setAssignTime(new Date());
                //分配状态 1=已分配
                saleChance.setState(StateStatus.STATED.getType());
                //开发状态 1=开发中
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }
        } else {//存在
            //判断修改后的值是否存在
            if (StringUtils.isBlank(saleChance.getAssignMan())) {//修改前有值，修改后无值
                //assignTime指派时间 设置为null
                saleChance.setAssignTime(null);
                //分配状态 0=未分配
                saleChance.setState(0);
                //开发状态 0=未开发
                saleChance.setDevResult(0);
            } else {//修改前后都有值
                //判断修改前后是否是同一个用户
                if (!saleChance.getAssignMan().equals(temp.getAssignMan())) {
                    //更新指派时间
                    saleChance.setAssignTime(new Date());
                } else {
                    //设置指派时间为原来的指派时间
                    saleChance.setAssignTime(temp.getAssignTime());
                }

            }
        }
        // 3.执⾏更新 判断结果
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1, "营销机会数据更新失败！");

    }

    /**
     * 1.参数校验
     * customerName客户名称:⾮空
     * linkMan联系人:⾮空
     * linkPhone联系号码:⾮空 11位⼿机号
     *
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkSaleChanceParams(String customerName, String linkMan, String linkPhone) {
        //customerName客户名称:⾮空
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "客户名称不能为空");
        //linkMan联系人:⾮空
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "联系人不能为空");
        //linkPhone联系号码:⾮空
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "联系号码不能为空");
        //linkPhone联系号码:11位⼿机号
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone), "联系号码格式不正确");
    }

    /**
     * 删除营销机会
     *
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids) {
        //判断ID是否为空
        AssertUtil.isTrue(ids == null && ids.length < 1, "待删除记录不存在");
        //执行删除操作,判断影响的行数
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) != ids.length, "营销机会数据删除失败");
    }

    /**
     * 更新营销机会的开发状态
     *
     * @param id
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        AssertUtil.isTrue(null == id, "待更新记录不存在!");
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null == saleChance, "待更新记录不存在!");
        //设置开发状态
        saleChance.setDevResult(devResult);
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) != 1, "开发状态更新失败!");
    }

}
