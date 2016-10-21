$(function () {

    buildQR('dddsfdsfdsfdsfdsfdssdddddddddddddddddddddddddddddddddddddddddfffewfsdfdsfdsfdsfdsfdsfdsfdssdddddddddddddddddddddddddddddddddddddddddfffewfsds');

    var e_quick_login = $('#quick-login'), e_pwd_login = $('#pwd-login'), box_quick_login = $('#qr-login-box'), box_pwd_login = $('#pwd-login-box');
    e_quick_login.click(function (e) {
        box_quick_login.addClass('active');
        box_pwd_login.removeClass('active');

    });

    e_pwd_login.click(function () {
        box_pwd_login.addClass('active');
        box_quick_login.removeClass('active');
    });


});

var qrId;

function getQR() {

    //获取二维码结构
    var path = '/qrController/obtainQrIdAndCode';
    $.get(path,function (resp) {
        buildQR(resp);
        qrId = resp.qrId;
    });
}

function checkAuth() {
    var ca_id = setInterval( function () {
        $.get('./qrController/authOtp', {qrId: qrId},function (resp) {

        });

    });

}

function buildQR(text) {
    $('#qr-box').qrcode({
        padding: '10',
        width: '130',
        height: '130',
        text: text
    });
}