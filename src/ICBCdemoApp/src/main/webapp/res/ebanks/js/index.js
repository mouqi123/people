/**
 * 通付盾HUE websdk
 * Copyright 2016, payegis.com
 * 流程：
 * 1.初始化sdk，接受相关参数，并完成相关参数的校验
 *   接受参数 token (事务号)【必填】，用于给HUE后台传递相关业务信息
 *   接受参数 backTo (回调地址)【必填】，用于完成HUE认证后，把信息通知到业务系统
 *   接受参数 businessType (业务类型)【必填】，目前只支持bind,auth 类型，用于C平台判断跳转地址
 *   接受参数 openType (打开方式)【可选】，默认_self(本页面跳转)，_blank(新页面跳转),_iframe(iframe浮动窗口打开)
 *   接受参数 iframeId (打开方式)【可选】，如果是iframe浮动窗口打开，则需要传iframeId
 *   接受参数 formId (打开方式)【可选】，成功回调发送post的form表单id，如果不传则会自动创建一个id为Hue_form的form
 *   接受参数 downloadUrl (应用下载地址)【可选】，用于绑定时扫码下面的下载按钮地址，默认获取当前网址
 *   接受参数 host (C平台入口地址)【可选】，可配置C平台入口地址
 *   接受参数 parent (返回地址)【可选】，定义从C平台返回到业务系统的url，默认获取当前网址
 * 2.准备条件，确保相关参数的已经正确的获取，主要是确保iframe的正常获取，考虑dom加载的异步流程
 *   并且修改iframe的宽高为现在C平台页面的宽高，确保嵌入的C平台页面显示正常
 * 3.准备工作就绪，拼接参数，发送跳转到C平台请求，根据openType判断跳转方式，
 *   如果是本页跳转用本页嵌入form post跳转，等待认证成功后，C平台调用回调地址，流程结束。
 *   如果是新页面跳转用window.open，等待认证成功后，C平台调用回调地址，流程结束。（有兼容性问题，暂不支持）
 *   如果是iframe浮动窗口打开，则只能用url get跳转，然后等待iframe通过postmessage发送成功消息回调，验证message的合法性，
 *   解析message的数据为三个参数status，message，token，然后调用回调地址，流程结束。
 */
(function( window ) {
    function Hue(){
        var HUE_MESSAGE_FORMAT = /^\{(.)*\}$/;
        //var HUE_ERROR_FORMAT = /^ERR\:$/;

        var host;
        var token;
        var backTo;
        var downloadUrl;
        var parent;
        var openType;
        var businessType;
        var iframeId;
        var iframe;
        var formId;
        /*目前C平台的页面宽度*/
        var iframeWidth=736;
        var iframeHeight=550;
        /*初始化web sdk*/
        function init(options) {
            if (options) {

                openType=options.openType||'_self';
                formId=options.formId||'Hue_form';
                downloadUrl=options.downloadUrl||window.location.href;
                parent=options.parent||window.location.href;

                if(options.host){
                    host=options.host;
                }else{
                    throwError('host参数必填！');
                }

                if(options.token){
                    token=options.token;
                }else{
                    throwError('token参数必填！');
                }

                if(options.businessType){
                    businessType=options.businessType;
                }else{
                    throwError('businessType参数必填！');
                }

                if(options.backTo){
                    backTo=options.backTo;
                }else{
                    throwError('backTo参数必填！');
                }

                if(openType=='_iframe'){
                    if(options.iframeId){
                        iframeId=options.iframeId;
                        iframe=getIframe(iframeId);
                        if (iframe) {
                            ready();
                        } else {
                            if(document && document.getElementsByTagName && document.getElementById && document.body) {
                                //dom已经加载完成
                                onDOMReady();
                            }else{
                                // 等待dom加载完毕，再执行收集iframe，不用匿名函数传参，已便不能正常删除事件
                                onDocumentReady(onDOMReady);
                            }
                        }
                    }else{
                        throwError('openType为_iframe时，请设置iframeId！');
                    }
                }else{
                    ready();
                }

            }else{
                throwError('请以对象形式传递相关参数！');
            }
        }
        /*当dom加载完后再获取iframe*/
        function onDOMReady() {
            iframe = getIframe(iframeId);
            if(!iframe){
                throwError(
                    iframeId+'不是一个iframe元素id，' +
                    '请确保该页面已经包含<iframe id="'+iframeId+'"></iframe> ' +
                    '请在https://www.payegis.com上查看相关文档。'
                );
            }
            //iframe获取完毕，准备工作已完成
            ready();

            //清除document的关于onDOMReady的onreadystatechange、DOMContentLoaded事件
            offDocumentReady(onDOMReady);

        }
        /*
         * 通过事件的origin是否来自host，和数据格式是否是HUE数据格式，
         * 判断事件是否是HUE发来的事件，返回true或false
        */
        function isHueMessage(event) {
            return Boolean(
                new RegExp('^'+event.origin,'g').test(host) &&
                typeof event.data === 'string' &&
                (
                event.data.match(HUE_MESSAGE_FORMAT)
                )
            );
        }
        function POST(url,params,formId,iframe){
            var formId=formId||'Hue_form';
            var inputs=[];
            for(var name in params){
                var input = document.createElement('input');
                input.type = 'hidden';
                input.name = name;
                input.value = params[name];
                inputs.push(input);
            }


            //用户可以自定义form，已便提交其他参数
            var form = document.getElementById(formId);

            // 如果form不存在，则自动创建一个
            if (!form) {
                form = document.createElement('form');

                if(iframe){
                    // 创建的form插在iframe的后面
                    iframe.parentElement.insertBefore(form, iframe.nextSibling);
                }else{
                    document.body.appendChild(form);
                }

            }

            // 用post提交到backto url
            form.method = 'POST';
            form.action = url;
            for(var i=0;i<inputs.length;i++){
                var _input=inputs[i];
                // 添加token字段到form
                form.appendChild(_input);
            }

            // 提交表单
            form.submit();
        }
        function doBack(response) {
            /*
             * response [HUE COMPLETE]
             * 接受C平台消息成功，通过backTo的类型，去做相应回调，
             * 如果backTo为url，则直接本页创建form post跳转，
             * 如果backTo为函数，则把参数传入回调，并调用
            */
            if(typeof backTo ==='string'){
                POST(backTo,{token:token},formId,iframe);
            }else if(typeof backTo ==='function'){
                response.token=token;
                backTo(response);
            }else{
                throwError('backTo参数的值类型必须是string或者function！');
            }

        }
        /*window触发message事件回调函数*/
        function onReceivedMessage(event) {
            if (isHueMessage(event)) {
                //如果该消息是来自于HUE C平台
                doBack(JSON.parse(event.data));

                //接受完毕删除window上的message事件
                offWindowMessage(onReceivedMessage);
            }
        }
        /*当所有准备工作都已就绪的时候开始发起请求*/
        function ready(){
            var url;
            switch (openType){
                case '_self':
                    if(typeof backTo ==='string'){
                        //encodeURIComponent
                        POST(host,{
                            token:encodeURIComponent(token),
                            openType:'_self',
                            businessType:encodeURIComponent(businessType),
                            backTo:encodeURIComponent(backTo),
                            parent:encodeURIComponent(parent),
                            downloadUrl:encodeURIComponent(downloadUrl)
                        });
                    }else{
                        throwError('openType为_self的时候，backTo参数的值类型必须是string！');
                    }

                    break;
                //case '_blank':
                //    window.open(url);
                //    break;
                case '_iframe':
                    url=[
                        host, '?token=', encodeURIComponent(token),
                        '&openType=_iframe',
                        '&businessType=',encodeURIComponent(businessType),
                        '&parent=',encodeURIComponent(parent),
                        '&downloadUrl=',encodeURIComponent(downloadUrl)
                    ].join('');
                    iframe.src=url;
                    //iframe方式打开，监听window上的message事件，等待成功后，C平台通过iframe发来消息
                    onWindowMessage(onReceivedMessage);
                    break;
                default:
                    throw new Error(
                        'Hue Web SDK error: openType的值，必须为_self或者_iframe！'
                    );
                    break;
            }
        }
        /*确保获取的元素为存在的iframe标签，不然返回false*/
        function getIframe(id){
            var dom= document.getElementById(id);
            if(dom&&dom.tagName=='IFRAME'){
                //默认设置iframe的宽高为C平台的页面宽高，以防止嵌入显示异常
                dom.style.width=iframeWidth+"px";
                dom.style.height=iframeHeight+"px";
                return dom;
            }else{
                return false;
            }
        }
        /*统一抛出异常方法*/
        function throwError(message, url) {
            throw new Error(
                'Hue Web SDK error: ' + message +
                (url ? ('\n' + 'See ' + url + ' for more information') : '')
            );
        }
        /*
        * 绑定事件有兼容性问题，故做兼容性封装，on，off函数，方便绑定、删除事件
        * 在现代浏览器中，使用
        * target.addEventListener(type, listener, useCapture);
        * target： 文档节点、document、window 或 XMLHttpRequest。
        * type： 字符串，事件名称，不含“on”，比如“click”、“mouseover”、“keydown”等。
        * listener ：实现了 EventListener 接口或者是 JavaScript 中的函数。
        * useCapture ：是否使用捕捉，一般用 false 表示在冒泡阶段调用事件处理程序，true，表示在捕获阶段调用事件处理。
        * 例如：document.getElementById("testText").addEventListener("keydown", function (event) { alert(event.keyCode); }, false);
        *
        * 在IE中，使用
        * target.attachEvent(type, listener);
        * target： 文档节点、document、window 或 XMLHttpRequest。
        * type： 字符串，事件名称，含“on”，比如“onclick”、“onmouseover”、“onkeydown”等。
        * listener ：实现了 EventListener 接口或者是 JavaScript 中的函数。
        * 例如：document.getElementById("txt").attachEvent("onclick",function(event){alert(event.keyCode);});
        *
        * 注意：
        * 在删除某个target的事件的时候，removeEventListener、detachEvent，接收的参数必须和addEventListener，attachEvent的参数一致
        * 并且不能用匿名函数，不然两次参数肯定不一样，就会导致不能正常删除事件
        * */
        function on(context, event, fallbackEvent, callback) {
            if ('addEventListener' in window) {
                context.addEventListener(event, callback, false);
            } else {
                context.attachEvent(fallbackEvent, callback);
            }
        }

        function off(context, event, fallbackEvent, callback) {
            if ('removeEventListener' in window) {
                context.removeEventListener(event, callback, false);
            } else {
                context.detachEvent(fallbackEvent, callback);
            }
        }

        /*给document，绑定dom加载完成事件，自定义回调函数*/
        function onDocumentReady(callback) {
            on(document, 'DOMContentLoaded', 'onreadystatechange', callback);
        }
        /*给document，删除dom加载完成事件，自定义回调函数*/
        function offDocumentReady(callback) {
            off(document, 'DOMContentLoaded', 'onreadystatechange', callback);
        }
        /*给window，绑定message事件，自定义回调函数*/
        function onWindowMessage(callback) {
            on(window, 'message', 'onmessage', callback);
        }
        /*给window，删除message事件，自定义回调函数*/
        function offWindowMessage(callback) {
            off(window, 'message', 'onmessage', callback);
        }

        return {
            init:init
        };
    }
    //var ajax=require('./src/jx');
    //var elm_result=document.getElementById('result');
    //
    //elm_result.innerHTML="0";
    //
    //ajax.load('sample.json?a=1&b=2',function(data){
    //    /*
    //     ... do what you want with 'data' variable ...
    //     */
    //    console.log(data);
    //    elm_result.innerHTML = '"' + data.foo + '"';
    //},'json','POST');

    // EXPOSE
    if ( typeof define === "function" && define.amd ) {
        //for amd
        define(function() { return Hue(); });
    } else if ( typeof module !== "undefined" && module.exports ) {
        //for commonjs
        module.exports = Hue();
    } else {
        //for global
        window.Hue = Hue();
    }


})( window );
