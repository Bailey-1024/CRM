package com.lgy.service;

import com.lgy.base.BaseService;
import com.lgy.base.ResultInfo;
import com.lgy.dao.UserMapper;
import com.lgy.dao.UserRoleMapper;
import com.lgy.model.UserModel;
import com.lgy.utils.AssertUtil;
import com.lgy.utils.Md5Util;
import com.lgy.utils.PhoneUtil;
import com.lgy.utils.UserIDBase64;
import com.lgy.vo.User;
import com.lgy.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User, Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 用户登录
     *
     * @param userName
     * @param userPwd
     */
    public UserModel userLogin(String userName, String userPwd) {
        //1、验证参数
        checkLoginParams(userName, userPwd);
        //2、根据用户名，查询用户对象
        User user = userMapper.queryUserByUserName(userName);
        //3、判断用户是否存在（用户对象为空，记录不存在，方法结束）
        AssertUtil.isTrue(user == null, "用户不存在！");
        //4、判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象中的用户密码
        checkUserPwd(userPwd, user.getUserPwd());
        //返回构建用户对象
        UserModel userModel = buildUserInfo(user);
        return userModel;
    }

    /**
     * 构建需要返回给用户端的对象
     *
     * @param user
     */
    private UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
        //userModel.setUserId(user.getId());
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * ⽤户密码修改
     * 1. 参数校验
     * ⽤户ID：userId ⾮空 ⽤户对象必须存在
     * 原始密码：oldPassword ⾮空 与数据库中密⽂密码保持⼀致
     * 新密码：newPassword ⾮空 与原始密码不能相同
     * 确认密码：confirmPassword ⾮空 与新密码保持⼀致
     * 2. 设置⽤户新密码
     * 新密码进⾏加密处理
     * 3. 执⾏更新操作
     * 受影响的⾏数⼩于1，则表示修改失败
     * 注：在对应的更新⽅法上，添加事务控制
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId, String oldPassword, String newPassword, String repeatPassword) {
        //通过userId获取用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        //1、参数校验
        checkPasswordParams(user, oldPassword, newPassword, repeatPassword);
        //2、设置用户新密码
        user.setUserPwd(Md5Util.encode(newPassword));
        //3、执行更新操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "用户密码更新失败");
    }

    /**
     * 验证⽤户密码修改参数
     * 原始密码：oldPassword ⾮空 与数据库中密⽂密码保持⼀致
     * 新密码：newPassword ⾮空 与原始密码不能相同
     * 确认密码：confirmPassword ⾮空 与新密码保持⼀致
     *
     * @param user
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd
     */
    private void checkPasswordParams(User user, String oldPwd, String newPwd, String repeatPwd) {
        //原始密码 非空校验
        AssertUtil.isTrue(oldPwd == null, "原始密码不能为空");
        //原始密码加密后与数据库密码对比
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPwd))), "密码错误");
        //新密码非空校验
        AssertUtil.isTrue(newPwd == null, "新密码不能为空");
        //新密码与旧密码对比
        AssertUtil.isTrue(newPwd.equals(oldPwd), "新密码不能与旧密码相同");
        //重复密码 非空校验
        AssertUtil.isTrue(repeatPwd == null, "重复密码不能为空");
        //重复密码与新密码对比
        AssertUtil.isTrue(!(repeatPwd.equals(newPwd)), "重复密码与新密码不同");
    }

    /**
     * 验证登录密码
     *
     * @param userPwd 前台传递的密码
     * @param pwd     数据库的密码
     */
    private void checkUserPwd(String userPwd, String pwd) {
        //数据库中的密码时经过加密的，将前台传递的密码先加密，在于数据库中的密码进行对比
        userPwd = Md5Util.encode(userPwd);
        //比较密码
        AssertUtil.isTrue(!userPwd.equals(pwd), "用户密码不正确");
    }

    /**
     * 验证用户登录参数
     *
     * @param userName
     * @param userPwd
     */
    private void checkLoginParams(String userName, String userPwd) {
        //判断姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户姓名不能为空");
        //判断密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空");
    }

    /**
     * 查询所有的销售人员
     *
     * @return
     */
    public List<Map<String, Object>> queryAllSales() {
        return userMapper.queryAllSales();
    }

    /**
     * 添加⽤户
     * 1. 参数校验
     * ⽤户名 ⾮空 唯⼀性
     * 邮箱 ⾮空
     * ⼿机号 ⾮空 格式合法
     * 2. 设置默认参数
     * isValid 1
     * creteDate 当前时间
     * updateDate 当前时间
     * userPwd 123456 -> md5加密
     * 3. 执⾏添加，判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user) {
        //1. 参数校验
        checkUserParams(user.getUserName(),user.getEmail(),user.getPhone(),null);
        //2. 设置默认参数
        //isValid 1
        user.setIsValid(1);
        //creteDate 当前时间
        user.setCreateDate(new Date());
        //updateDate 当前时间
        user.setUpdateDate(new Date());
        //userPwd 123456 -> md5加密
        user.setUserPwd(Md5Util.encode("123456"));
        //3. 执⾏添加，判断结果
        AssertUtil.isTrue(userMapper.insertSelective(user)< 1,"添加失败");

        /**
         * 用户角色关联
         *  用户ID
         *      userId
         *  角色ID
         *      roleIds
         */
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 用户角色关联
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //通过角色Id查询角色记录
        Integer count =userRoleMapper.countUserRoleByUserId(userId);
        //判断角色记录是否存在
        if (count>0){
            //如果角色记录存在，则删除该用户对应的角色记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色分配失败");
        }
        //判断用户id是否存在，如果存在，则添加该用户对应的角色记录
        if (StringUtils.isNotBlank(roleIds)){
            //将用户角色数据设置到集合中，执行批量添加
            List<UserRole> userRoleList=new ArrayList<>();
            //将角色Id字符串转换为数组
            String[] roleIdsArray=roleIds.split(",");
            //遍历数组，得到对应的用户角色对象，并设置到集合中
            for (String roleId:roleIdsArray){
                UserRole userRole=new UserRole();
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                //设置到集合中
                userRoleList.add(userRole);
            }
            //批量添加用户角色记录
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList)!=userRoleList.size(),"用户更新失败");
        }
    }

    /**
     * 更新⽤户
     * 1. 参数校验
     * id ⾮空 记录必须存在
     * ⽤户名 ⾮空 唯⼀性
     * email ⾮空
     * ⼿机号 ⾮空 格式合法
     * 2. 设置默认参数
     * updateDate
     * 3. 执⾏更新，判断结果
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        //1. 参数校验
        //id ⾮空 记录必须存在
        AssertUtil.isTrue(user.getId()==null,"待更新记录不存在");
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(temp==null,"待更新记录不存在");
        //⽤户名 ⾮空 唯⼀性
        //email ⾮空
        //⼿机号 ⾮空 格式合法
        checkUserParams(user.getUserName(), user.getEmail(), user.getPhone(),user.getId());
        //2. 设置默认参数
        //updateDate
        user.setUpdateDate(new Date());
        //3. 执⾏更新，判断结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)!=1,"更新失败");
        /**
         * 用户角色关联
         *  用户ID
         *      userId
         *  角色ID
         *      roleIds
         */
        relationUserRole(user.getId(),user.getRoleIds());


    }
    /**
     * 1. 参数校验
     * ⽤户名 ⾮空 唯⼀性
     * 邮箱 ⾮空
     * ⼿机号 ⾮空 格式合法
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUserParams(String userName, String email, String phone,Integer userId) {
        //    ⽤户名 ⾮空 唯⼀性
        AssertUtil.isTrue(userName==null,"用户名不能为空");
        User user = userMapper.queryUserByUserName(userName);
        // 如果是添加操作，数据库是没有数据的，数据库中只要查询到⽤户记录就表示不可⽤
        // 如果是修改操作，数据库是有数据的，查询到⽤户记录就是当前要修改的记录本身就表示可⽤，否则不可⽤
        // 数据存在，且不是当前要修改的⽤户记录，则表示其他⽤户占⽤了该⽤户名
        AssertUtil.isTrue(null !=user && !(user.getId().equals(userId)), "该⽤户已存在，请重新输入！");
        //    邮箱 ⾮空
        AssertUtil.isTrue(email==null,"邮箱不能为空");
        //    ⼿机号 ⾮空 格式合法
        AssertUtil.isTrue(phone==null,"手机号不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号格式错误");
    }


    /**
     * 用户删除
     * @param ids
     * @return
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByIds(Integer[] ids){
        AssertUtil.isTrue(null==ids || ids.length == 0,"请选择待删除的⽤户记录!");
        AssertUtil.isTrue( userMapper.deleteBatch(ids) != ids.length,"⽤户记录删除失败!");
        //遍历用户Id的数组
        for (Integer userId:ids){
            //通过角色Id查询角色记录
            Integer count =userRoleMapper.countUserRoleByUserId(userId);
            //判断角色记录是否存在
            if (count>0){
                //如果角色记录存在，则删除该用户对应的角色记录
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色分配失败");
            }
        }
    }
}
