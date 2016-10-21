<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
<head>
    <title>登录演示</title>
    <link rel='stylesheet' href='/css/style.css'/>
    <link rel='stylesheet' href='/bower_components/bootstrap/dist/css/bootstrap.min.css'/>


</head>
<body style="width: 100%; min-width: 500px;">

<header
style=" max-width: 1200px; padding: 10px 50px; height: 70px; line-height: 50px; font-size: 20px; font-weight: bold; margin: 0 auto;">
    众人科技-登录演示
</header>

<div class="contain" style="overflow: hidden; background-color: #3aa6ff; height: 600px">
    <div class="content" style="height: 300px; max-width: 1200px; margin: 0 auto; ">
        <div style="width: 300px; height: 300px; margin: 120px 60px 0 auto; box-shadow: 1px 1px 5px #666;">
            <div id="qr-login-box" class="active"
                 style="padding: 30px; height: 100%; background-color: #ecf6ff; color: black; box-sizing: border-box;position: relative;">
                <!--登录方式提示-->
                <div class="login-tip" style="position: absolute;top: 5px;right: 58px;">
                    <div class="poptip">
                        <div class="poptip-arrow">
                            <em></em>
                            <span></span>
                        </div>
                        <div class="poptip-content" style="color: #df9c1f; font-size: 12px; font-weight: 400">
                            普通登录
                        </div>
                    </div>
                </div>

                <!--切换登录方式-->
                <div class="login-switch" style="position: absolute; top: 0; right: 0;" onselectstart="return false;">
                    <i id="pwd-login" class="iconfont static"></i>
                </div>
                <!--标题-->
                <div class="login-title">
                    <span>扫码登录</span>
                </div>

                <!--登录框区域-->
                <div style="width: 150px; height: 150px; top: 75px; left: 75px; position: absolute;">

                    <!--loading-->
                    <div id="qr-loading"
                         style="position: absolute;top: 0; left: 0;width: 100%; height: 100%; background-color: #ecf6ff">
                        <div class=""
                             style="height: 100%; background: transparent  url(https://img.alicdn.com/tps/TB1R5zYKVXXXXb7XVXXXXXXXXXX-32-32.gif) no-repeat 50% 50%;"></div>

                    </div>

                    <!--报错-->
                    <div id="qr-err"
                         style="width: 100%; height: 100%; background-color: rgba(255,255,255,.9); position: absolute;top: 0; left: 0; ">
                        <div style="text-align: center;">
                            <h6 id="qr-err-smg" style="padding: 50px 0 10px;">二维码已失效</h6>
                            <a href="javascript:;" class="refresh J_QRCodeRefresh" data-spm-anchor-id="a2107.1.0.0">请点击刷新</a>
                        </div>
                    </div>

                    <!--二维码-->
                    <div id="qr-box"
                    style="background-color: white;position: absolute;top: 0; left: 0;padding: 10px; box-sizing: border-box;">
                    </div>
                </div>

                <!--扫码提示-->
                <div style="position: absolute; bottom: 20px; width: 100%; left: 65px;" class="qrcode-desc"
                     data-spm="25847036">
                    <i class="iconfont"></i>

                    <p style="font-size: 12px; color: #6c6c6c; line-height: 35px;"><font class="ft-gray">打开 </font>
                        <a target="_blank" class="light-link">S盾</a>
                        <span class="ft-gray">扫一扫登录</span>
                    </p>

                </div>
            </div>

            <div id="pwd-login-box"
                 style="padding: 30px; height: 100%; background-color: #ecf6ff; color: black; box-sizing: border-box;position: relative;">
                <!--登录方式切换提示-->
                <div class="login-tip" style="position: absolute;top: 5px;right: 58px;">
                    <div class="poptip">
                        <div class="poptip-arrow">
                            <em></em>
                            <span></span>
                        </div>
                        <div class="poptip-content" style="color: #df9c1f; font-size: 12px; font-weight: 400">
                            扫码登录
                        </div>
                    </div>
                </div>

                <!--切换登录方式-->
                <div class="login-switch" style="position: absolute; top: 0; right: 0;" onselectstart="return false;">
                    <i id="quick-login" class="iconfont quick"></i>
                </div>
                <!--标题-->
                <div class="login-title" style="padding-top: 10px;">
                    <span>普通登录</span>
                </div>

                <!--登录框区域-->
                <div style="padding-top: 50px;">
                    <form class="bs-example bs-example-form" role="form">
                        <div class="input-group">
                            <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                            <input type="text" class="form-control" placeholder="请输入账号">
                        </div><br>

                        <div class="input-group">
                            <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                            <input type="password" class="form-control" placeholder="请输入密码">
                        </div><br>

                        <div class="input-group" style="display: block;">
                            <a class="btn btn-success form-control">登录</a>
                        </div><br>
                    </form>
                </div>

            </div>

        </div>
    </div>
</div>


<div class="footer" style="padding-top: 30px;">
    <p class="text-center">Copyright @ 2016
        <a href="http://www.people2000.net/" title="众人科技" target="_blank">People2000 </a>
    </p>
</div>
    <script src='<%=request.getContextPath()%>/bower_components/jquery/dist/jquery.min.js'></script>
    <script src='<%=request.getContextPath()%>/bower_components/jquery_qrcode/jquery.qrcode.min.js'></script>
<script src="/js/login.js"></script>

</body>
 
