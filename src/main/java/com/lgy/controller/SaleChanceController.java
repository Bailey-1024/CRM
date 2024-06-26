package com.lgy.controller;

import com.lgy.annotation.RequiredPermission;
import com.lgy.base.BaseController;
import com.lgy.base.ResultInfo;
import com.lgy.enums.StateStatus;
import com.lgy.query.SaleChanceQuery;
import com.lgy.service.SaleChanceService;
import com.lgy.utils.CookieUtil;
import com.lgy.utils.LoginUserUtil;
import com.lgy.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
@RequestMapping("/sale_chance")
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;

    /**
     * 多条件分页查询营销机会
     * @param query
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    @RequiredPermission(code="101001")
    public Map<String, Object>querySaleChanceByParams(SaleChanceQuery query,Integer flag,HttpServletRequest req){
        //判断flag的值
        if (flag!=null&&flag==1){
            //查询客户开发机会
            //设置分配状态
            query.setState(StateStatus.STATED.getType());
            //设置指派人（当前登录用户的id）
            //从cookie中获取当前用户的id
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
            query.setAssignMan(userId);
        }

        return saleChanceService.querySaleChanceByParams(query);
    }

    /**
     * 进入营销机会管理界面
     * @return
     */
    @RequiredPermission(code="1010")
    @RequestMapping("/index")
    public String index(){
        return "/saleChance/sale_chance";
    }

    /**
     * 添加营销机会
     * @param saleChance
     * @return
     */
    @RequiredPermission(code="101002")
    @PostMapping("/add")
    @ResponseBody
    public ResultInfo addSaleChance(SaleChance saleChance, HttpServletRequest req){
        //从cookie中取得当前登录的用户名
        String userName = CookieUtil.getCookieValue(req, "userName");
        //设置用户名营销机会对象
        saleChance.setCreateMan(userName);
        //调用Service层的添加方法
        saleChanceService.addSaleChance(saleChance);
        return success("营销机会数据添加成功");

    }

    /**
     * 更新营销机会
     * @param saleChance
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiredPermission(code="101004")
    public ResultInfo updateSaleChance(SaleChance saleChance){
        //调用Service层的添加方法
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功");

    }


    /**
     * 机会数据添加与更新⻚⾯视图转发
     * @param id
     * @param req
     * @return
     */
    @RequestMapping("/toSaleChancePage")
    public String toSaleChancePage(Integer id, HttpServletRequest req){
        //判断id是否为空
        if (id!=null){
            //通过id查询营销机会数据
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //将数据设置到请求域中
            req.setAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 删除营销机会
     * @param ids
     * @return
     */
    @RequiredPermission(code="101003")
    @PostMapping("/delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会数据删除成功");
    }


    /**
     * 更改营销机会的状态
     * @param id
     * @param devResult
     * @return
     */
    @RequiredPermission(code="101004")
    @RequestMapping("/updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("开发状态更新成功!");
    }
}
