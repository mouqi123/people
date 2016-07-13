(function( window ) {
    jQuery.extend({
        visibleHidden: function(e) {
            e.css({
                visibility: "hidden"
            });
            return false;
        },
        visibleShow: function(e) {
            e.css({
                visibility: "visible"
            });
            return false;
        }
    });
    function controlPop(str_type,$_pop,$_close,$_mask,num_speed,bool_maskClose,closeCallback){
        /*
         * @位置： 常用界面交互方法；
         * @名字：  controlPop；
         * @翻译：  控制弹窗（ 类型，弹框主体，关闭按钮，遮罩层 ）；
         * @参数：  controlPop(type,pop,close,mask,speed，maskClose)【支持对象传参，以下是属性】
         *         {
         *         type（字符串）：【必填】‘show’，显示，‘hide’，隐藏
         *         pop（$）：【必填】jquery选中的弹框主体
         *         close（$）：【可选】jquery选中的关闭按钮
         *         mask（$）：【可选】jquery选中的自定义遮罩层，默认是黑色，透明度0.6
         *         speed（数字）：【可选】显示缓动时间,默认为0，也可传”fast“，”slow“
         *         maskClose（布尔）：【可选】默认false点击遮罩不关闭弹窗，true，点击遮罩关闭弹窗
         *         };
         * @功能：  显示或隐藏弹窗，自带遮罩，根据窗口大小自动居中；
         * @返回：  无；
         * @实例：  /test-html/3.2/UIInteractive/controlPop.html；
         * @需要：  parseArgus；
         * @备注：  如果弹框主体里有表单重置表单；
         */

        var $mainPop = $_pop,
            $mask = $_mask||$('<div class="senroPopMask"></div>'),
            maskColor=$_mask?$mask.css('backgroundColor'):'#000',
            maskOpacity=$_mask?$mask.css('opacity'):0.6,
            $close=$_close||null,
            $body=$('body'),
            speed=num_speed|| 0,
            hasMask= $_mask ? true : false;

        //如果有表单重置表单
        if($mainPop.find('form').length!=0){
            $mainPop.find('form')[0].reset();
        }
        if($mainPop.find('iframe').length!=0){
            $mainPop.find('iframe').hide();
            setTimeout(function(){
                $mainPop.find('iframe').show();
            },300);
        }
        $mask.unbind('click');
        if($close) {
            $close.unbind('click');
        }
        //reset Pops
        $mainPop.css({
            zIndex: "9999",
            margin: 0
        });
        $mask.css({
            position: "absolute",
            left: "0",
            top: "0",
            zIndex: "9998",
            backgroundColor: maskColor,
            opacity: maskOpacity
        });
        $(window).resize(function(){
            if($mainPop.is(':visible')){
                setPop();
            }
            return false;
        });
        if(str_type=="show"){
            setPop();
            if(!hasMask){
                $body.append($mask);
            }
            $mask.fadeIn(speed);

        }else if(str_type=="hide"){
            $mainPop.add($mask).fadeOut(speed,function(){
                if(!hasMask){
                    $('.senroPopMask').remove();
                }else{
                    $mask.remove();
                }
            });

        }
        if(bool_maskClose){
            $mask.click(function(){
                $mainPop.add($mask).fadeOut(speed,function(){
                    if(!hasMask){
                        $mask.remove();
                    }
                });

                return false;
            });
        }

        if($close){
            $close.click(function(){
                $mainPop.add($mask).fadeOut(speed,function(){
                    if(!hasMask){
                        $mask.remove();
                    }
                });
                closeCallback&&closeCallback();
                return false;
            });
        }

        function setPop(){
            var maskHeight = Math.max($body.height(),$(window).height()),
                maskWidth = $(window).width(),
                dialogTop = ($(window).height() - $mainPop.outerHeight(true)) / 2  + $(document).scrollTop(),
                dialogLeft = (maskWidth - $mainPop.outerWidth(true)) / 2;
            jQuery.visibleShow($mainPop);
            $mainPop.hide();
            $mainPop.css({
                top: dialogTop,
                left: dialogLeft
            }).fadeIn(speed);


            createMask($mask,maskWidth,maskHeight);


            return false;
        }
        function createMask($mask,maskWidth,maskHeight){

            $mask.css({
                height: maskHeight,
                width: maskWidth
            });
            return false;
        }
        return false;
    }//controlPop end
    // EXPOSE
    if ( typeof define === "function" && define.amd ) {
        define(function() { return controlPop; });
    } else if ( typeof module !== "undefined" && module.exports ) {
        module.exports = controlPop;
    } else {
        window.controlPop = controlPop;
    }


})( window );