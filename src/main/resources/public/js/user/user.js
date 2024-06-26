layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    var  tableIns = table.render({
        id : "userListTable",
        elem: '#userList',
        url : ctx+'/user/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'userName', title: '用户名称',align:"center"},
            {field: 'trueName', title: '真实姓名',  align:'center'},
            {field: 'email', title: '用户邮箱', align:'center'},
            {field: 'phone', title: '用户号码', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'updateDate', title: '修改时间', align:'center'},
            {title: '操作', templet:'#userListBar',fixed:"right",align:"center", minWidth:150}
        ]]
    });



    // 多条件搜索
    $(".search_btn").on("click",function(){
        table.reload("userListTable",{
            page: {
                curr: 1 //重新从第 1 页开始
            },
            where: {
                userName: $("input[name='userName']").val(),  //用户名
                email: $("input[name='email']").val(),  //邮箱
                phone: $("input[name='phone']").val(),   //电话
            }
        })
        //显示分配码（测试）
        // console.log($("#state").val())
    });


    //头工具栏事件
    table.on('toolbar(users)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case "add":
                openAddOrUpdateUserDialog();
                break;
            case "del":
                deleteUsers(checkStatus.data);
                break;
        };
    });

    /**
     * 行监听
     */
    table.on("tool(users)", function(obj){
        var layEvent = obj.event;
        if(layEvent === "edit") {
            //打开编辑用户的窗口
            openAddOrUpdateUserDialog(obj.data.id);
            // 查看是那个用户
            console.log(obj.data.id);
        }else if(layEvent === "del") {
            layer.confirm('确定删除当前数据？', {icon: 3, title: "用户管理"}, function (index) {
                $.post(ctx+"/user/delete",{ids:obj.data.id},function (data) {
                    if(data.code==200){
                        layer.msg("操作成功！");
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg, {icon: 5});
                    }
                });
            })
        }
    });


    // 打开添加机会数据页面
    function openAddOrUpdateUserDialog(uid){
        var url  =  ctx+"/user/toAddOrUpdateUserPage";
        var title="<h3>用户管理 -添加用户</h3>";
        console.log(uid);
        if(uid!=null && uid!=""){
            //更新操作
            url = url+"?id="+uid;
            title="用户管理 -更新用户";
        }
        layui.layer.open({
            title : title,
            type : 2,
            area:["650px","400px"],
            maxmin:true,
            content : url
        });
    }

    /**
     * 批量删除
     * @param datas
     */
    function deleteUsers(datas) {
        if(datas.length==0){
            layer.msg("请选择删除记录!", {icon: 5});
            return;
        }
        layer.confirm('确定删除选中的机会数据？', {
            btn: ['确定','取消'] //按钮
        }, function(index){
            layer.close(index);
            var ids= "ids=";
            for(var i=0;i<datas.length;i++){
                if(i<datas.length-1){
                    ids=ids+datas[i].id+"&ids=";
                }else {
                    ids=ids+datas[i].id
                }
            }
            $.ajax({
                type:"post",
                url:ctx+"/user/delete",
                data:ids,
                dataType:"json",
                success:function (data) {
                    if(data.code==200){
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg, {icon: 5});
                    }
                }
            })
        });
    }

});