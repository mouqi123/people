/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2015/12/29
 * Time: 17:54
 * To change this template use File | Settings | File Templates.
 */
    //通过页面固定宽度，动态设置viewport缩放网页
$(function () {
    var DEFAULT_WIDTH = 1024,// 页面的默认宽度
        deviceWidth = window.screen.width, // 设备的宽度
        initialScale=deviceWidth/DEFAULT_WIDTH;
    if(!(navigator.userAgent.indexOf('UCBrowser') > -1)) {
        $('meta[name="viewport"]').attr('content', 'user-scalable=yes, width=device-width, initial-scale='+initialScale);
    }else{
        $('meta[name="viewport"]').attr('content', 'user-scalable=yes,uc-fitscreen=no, width='+deviceWidth+', initial-scale='+0);
    }

});