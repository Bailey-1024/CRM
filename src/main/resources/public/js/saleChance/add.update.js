layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

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
     * 实现营销机会的添加与更新
     */
    form.on("submit(addOrUpdateSaleChance)", function (data) {
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});
        //弹出loading
        //默认地址为添加操作的地址
        var url = ctx + "/sale_chance/add";
        //找到营销机会的id
        var sid=$("input[name='id']").val();
        //如果营销机会的id不为空，则说明有数据，进行更新操作
        if (sid!=null && sid!="") {
            url = ctx + "/sale_chance/update";
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
                layer.msg(
                    res.msg, {
                        icon: 5
                    }
                );
            }
        });
        //阻止表单提交
        return false;
    });


    /**
     * 加载指派人的下拉框
     */
    $.ajax({
        type:"get",
        url:ctx+"/user/queryAllSales",
        data:{},
        success:function (data){
            //判断数据是否为空
            if (data!=null){
                //获取隐藏域设置的指派人id
                var assignManId=$("#assignManId").val();
                for (var i=0;i<data.length;i++){
                    var opt="";
                    //如果循环得到的ID与隐藏域的ID相等，则表示被选中
                    if (assignManId==data[i].id){
                        opt="<option value='"+data[i].id+"' selected >"+data[i].uname+"</option>";
                    }else{
                        //设置下拉选项
                        opt="<option value='"+data[i].id+"'>"+data[i].uname+"</option>";
                    }

                    //将下拉想设置到下拉框中
                    $("#assignMan").append(opt);
                }
            }
            //重新渲染下拉框的内容
            layui.form.render("select");
        }
    })



    $.post(ctx + "/user/queryAllCustomerManager", function (res) {
        for (var i = 0; i < res.length; i++) {
            if ($("input[name='man']").val() == res[i].id) {
                $("#assignMan").append("<option value=\"" + res[i].id + "\" selected='selected' >" + res[i].name + "</option>");
            } else {
                $("#assignMan").append("<option value=\"" + res[i].id + "\">" + res[i].name + "</option>");
            }
        }
        //重新渲染
        layui.form.render("select");
    });
});