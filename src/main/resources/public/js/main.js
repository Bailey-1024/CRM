layui.use(['element', 'layer', 'layuimini','jquery','jquery_cookie'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        $ = layui.jquery_cookie($);

    // 菜单初始化
    $('#layuiminiHomeTabIframe').html('<iframe width="100%" height="100%" frameborder="0"  src="welcome"></iframe>')
    layuimini.initTab();

    /**
     * 用户退出
     * 删除cookie
     *d
     */

    $(".login-out").click(function () {
        //删除cookie
        $.removeCookie("userIdStr",{domain:"localhost",path:"/"});
        $.removeCookie("userName",{domain:"localhost",path:"/"});
        $.removeCookie("trueName",{domain:"localhost",path:"/"});
        //跳转到登录首页（父窗口跳转）
        window.parent.location.href=ctx+"/index";
    });

});