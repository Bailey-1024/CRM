layui.use(['form', 'layer', 'formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    var formSelects = layui.formSelects;

    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 再执⾏关闭
        parent.layer.close(index);
    });

    /**
     * 监听submit事件
     * 实现用户的添加与更新
     */
    form.on("submit(addOrUpdateUser)", function (data) {
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});
        //弹出loading
        //默认地址为添加操作的地址
        var url = ctx + "/user/add";
        //找到营销机会的id
        var uid = $("input[name='id']").val();
        //如果营销机会的id不为空，则说明有数据，进行更新操作
        if (uid != null && uid != "") {
            url = ctx + "/user/update";
        }
        $.post(url, data.field, function (res) {
            if (res.code == 200) {
                setTimeout(function () {
                    top.layer.close(index);
                    top.layer.msg("操作成功！");
                    layer.closeAll("iframe");
                    //刷新父页面
                    parent.location.reload();
                }, 500);
            } else {
                layer.msg(res.msg, {icon: 5});
            }
        });
        //阻止表单提交
        return false;
    });


    /**
     * 加载角色下拉框数据
     */
    var userId=$("[name='id']").val();
    formSelects.config('selectId', {
        type: "post",//请求方式
        searchUrl: ctx + "/role/queryAllRoles?userId="+userId,
        //⾃定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //⾃定义返回数据中value的key, 默认 value
        keyVal: 'id'
    }, true);
});