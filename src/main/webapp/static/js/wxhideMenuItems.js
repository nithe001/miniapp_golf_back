$(function(){
    wx.ready(function(){
        wx.hideMenuItems({
            menuList: ["menuItem:share:appMessage", "menuItem:share:timeline", "menuItem:share:qq",
                "menuItem:share:weiboApp", "menuItem:share:facebook", "menuItem:share:QZone"] // 要隐藏的菜单项，只能隐藏“传播类”和“保护类”按钮，所有menu项见附录3
        });

    });
})