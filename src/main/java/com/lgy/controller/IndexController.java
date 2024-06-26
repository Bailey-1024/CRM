package com.lgy.controller;

import com.lgy.base.BaseController;
import com.lgy.service.PermissionService;
import com.lgy.service.UserService;
import com.lgy.utils.LoginUserUtil;
import com.lgy.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Resource
    private UserService userService;
    @Resource
    private PermissionService permissionService;
    /**
     * 系统登录页
     * @return
     */
    @RequestMapping("/index")
    public String index(){
        return "index";
    }
    // 系统界面欢迎页
    @RequestMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

    /**
     * 后端管理主⻚⾯
     * @return
     */
    @RequestMapping("/main")
    public String main(HttpServletRequest req){
        //获取cookie中的用户id
        Integer userId= LoginUserUtil.releaseUserIdFromCookie(req);
        //查询用户对象，设置session作用域
        User user = userService.selectByPrimaryKey(userId);
        req.getSession().setAttribute("user",user);

        //通过当前登录用户ID查询当前登录用户拥有的资源列表（查询对应资源的授权码）
        List<String> permissions=permissionService.queryUserHasRoleHasPermissionByUserId(userId);
        //将集合设置到session作用域中
        req.getSession().setAttribute("permissions",permissions);
        return "/main";
    }

}
