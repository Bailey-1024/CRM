package com.lgy.controller;

import com.lgy.base.BaseController;
import com.lgy.base.ResultInfo;
import com.lgy.query.CusDevPlanQuery;
import com.lgy.service.CusDevPlanService;
import com.lgy.service.SaleChanceService;
import com.lgy.vo.CusDevPlan;
import com.lgy.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("/cus_dev_plan")
@Controller
public class CusDevPlanController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private CusDevPlanService cusDevPlanService;


    /**
     * 客户开发主⻚⾯
     *
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "cusDevPlan/cus_dev_plan";
    }


    /**
     * 进⼊开发计划项数据⻚⾯
     *
     * @param req
     * @param id
     * @return
     */
    @RequestMapping("/toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(Integer id, HttpServletRequest req) {
        // 通过id查询营销机会数据
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
        // 将数据存到作⽤域中
        req.setAttribute("saleChance", saleChance);

        return "cusDevPlan/cus_dev_plan_data";
    }

    /**
     * 客户开发计划数据查询（分页多条件查询）
     * @param query
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> queryCusDevPlanByParams (CusDevPlanQuery query) {
        return cusDevPlanService.queryCusDevPlansByParams(query);
    }

    /**
     * 添加计划项
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("/add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("计划项添加成功!");
    }

    /**
     * 更新计划项
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项更新成功!");
    }

    /**
     * 删除计划项
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id){
        cusDevPlanService.deleteCusDevPlan(id);
        return success("计划项删除成功!");
    }




    /**
     * 进入添加或更新页面
     * @param req
     * @param sid
     * @return
     */
    @RequestMapping("/addOrUpdateCusDevPlanPage")
    public String addOrUpdateCusDevPlanPage(HttpServletRequest req,Integer sid,Integer id){
        req.setAttribute("sid",sid);
        //通过计划项Id查询数据
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
        req.setAttribute("cusDevPlan",cusDevPlan);
        return "/cusDevPlan/add_update";
    }

}
