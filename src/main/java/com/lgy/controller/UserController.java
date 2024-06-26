package com.lgy.controller;

import com.lgy.base.BaseController;
import com.lgy.base.ResultInfo;
import com.lgy.exceptions.ParamsException;
import com.lgy.model.UserModel;
import com.lgy.query.UserQuery;
import com.lgy.service.UserService;
import com.lgy.utils.LoginUserUtil;
import com.lgy.vo.SaleChance;
import com.lgy.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public ResultInfo userLogin(String userName, String userPwd) {
        ResultInfo resultInfo = new ResultInfo();
        //调用service层登录方法
        UserModel userModel = userService.userLogin(userName, userPwd);
        //设置ResultInfo的result的值（将数据返回给请求）
        resultInfo.setResult(userModel);
        ////通过try catch捕获service层的异常，如果service层抛出异常，则表示登录失败，否则登录成功
        //
        //try {
        //    //调用service层登录方法
        //    UserModel userModel = userService.userLogin(userName, userPwd);
        //    //设置ResultInfo的result的值（将数据返回给请求）
        //    resultInfo.setResult(userModel);
        //} catch (ParamsException p) {
        //    resultInfo.setCode(p.getCode());
        //    resultInfo.setMsg(p.getMsg());
        //    p.printStackTrace();
        //} catch (Exception e) {
        //    resultInfo.setCode(500);
        //    resultInfo.setMsg("登录失败");
        //}
        return resultInfo;
    }

    @PostMapping("/updatePassword")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest req, String oldPassword,
                                         String newPassword, String repeatPassword) {
        ResultInfo resultInfo = new ResultInfo();
        //获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //调用service层的密码修改方法
        userService.updateUserPassword(userId, oldPassword, newPassword, repeatPassword);
        //try {
        //    //获取userId
        //    Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //    //调用service层的密码修改方法
        //    userService.updateUserPassword(userId, oldPassword, newPassword, repeatPassword);
        //} catch (ParamsException p) {
        //    //设置状态码和提示信息
        //    resultInfo.setCode(p.getCode());
        //    resultInfo.setMsg(p.getMsg());
        //    p.printStackTrace();
        //} catch (Exception e) {
        //    resultInfo.setCode(500);
        //    resultInfo.setMsg("密码修改失败");
        //    e.printStackTrace();
        //}
        return resultInfo;
    }

    @RequestMapping("/toPasswordPage")
    public String toPasswordPage() {
        return "/user/password";
    }

    /**
     * 查询所有的销售人员
     */
    @RequestMapping("/queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales() {
    return userService.queryAllSales();
    }

    /**
     * 分页多条件查询用户列表
     * @param userQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> selectByParams(UserQuery userQuery){
        return userService.queryByParamsForTable(userQuery);
    }

    /**
     * 进入用户列表页面
     * @return
     */
    @RequestMapping("/index")
    public String index(){
        return "/user/user";
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public ResultInfo addUser(User user){
        userService.addUser(user);
        return success("用户添加成功");
    }
    /**
     * 更新用户
     * @param user
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户更新成功");
    }


    /**
     * 打开添加或修改用户的界面
     * @return
     */
    @RequestMapping("/toAddOrUpdateUserPage")
    public String toAddOrUpdateUserPage(Integer id,HttpServletRequest req){
        //判断id是否为空
        if (id!=null){
            //通过id查询营销机会数据
            User user = userService.selectByPrimaryKey(id);
            //将数据设置到请求域中
            req.setAttribute("userInfo",user);
        }
        return "/user/add_update";
    }

    /**
     * 用户删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteByIds(ids);
        return success("用户删除成功");
    }

}
