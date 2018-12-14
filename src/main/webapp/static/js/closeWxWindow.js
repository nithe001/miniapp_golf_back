$(function(){
    pushHistory();
    var bool=false;
    setTimeout(function(){
        bool=true;
    },500);
    window.addEventListener("popstate", function(e) {  //回调函数中实现需要的功能
        if(bool){
               // alert("我监听到了浏览器的返回按钮事件啦");//根据自己的需求实现自己的功能
            WeixinJSBridge.call('closeWindow');
        }
        pushHistory();
    }, false);
});
function pushHistory() {
    var state = {
        title: "title",
        url: window.location.href
    };
    window.history.pushState(state, state.title, state.url);
}