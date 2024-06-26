layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     * 表单的submit监听
     *      form.on(submit(按钮元素的lay-filter属性值)，function(data)){
     *
     *      }
     */
    form.on('submit(saveBtn)',function (data){
        // 所有元素的值
        console.log(data);
        //获取表单元素的内容
        var fieldData=data.field;
        //发送ajax请求，修改密码
        $.ajax({
            type:"post",
            url:ctx + "/user/updatePassword",
            data:{
                oldPassword:fieldData.old_password,
                newPassword:fieldData.new_password,
                repeatPassword:fieldData.again_password
            },
            dataType:"json",
            success:function (data){
                //判断是否成功
                if (data.code==200){
                    //修改成功后，用户自动退出系统
                    layer.msg("用户密码修改成功，系统将在三秒后退出...",function (){
                        //退出系统，删除响应的cookie
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/"});
                        $.removeCookie("userName",{domain:"localhost",path:"/"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/"});
                        //跳转到登录首页（父窗口跳转）
                        window.parent.location.href=ctx+"/index";
                    });
                }else{
                    layer.msg(data.msg);
                }
            }
        })
    })
});
