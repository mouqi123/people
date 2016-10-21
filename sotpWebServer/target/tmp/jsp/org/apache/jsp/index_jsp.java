package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write(" \n");
      out.write("<head>\n");
      out.write("    <title>登录演示</title>\n");
      out.write("    <link rel='stylesheet' href='/css/style.css'/>\n");
      out.write("    <link rel='stylesheet' href='/bower_components/bootstrap/dist/css/bootstrap.min.css'/>\n");
      out.write("\n");
      out.write("\n");
      out.write("</head>\n");
      out.write("<body style=\"width: 100%; min-width: 500px;\">\n");
      out.write("\n");
      out.write("<header\n");
      out.write("style=\" max-width: 1200px; padding: 10px 50px; height: 70px; line-height: 50px; font-size: 20px; font-weight: bold; margin: 0 auto;\">\n");
      out.write("    众人科技-登录演示\n");
      out.write("</header>\n");
      out.write("\n");
      out.write("<div class=\"contain\" style=\"overflow: hidden; background-color: #3aa6ff; height: 600px\">\n");
      out.write("    <div class=\"content\" style=\"height: 300px; max-width: 1200px; margin: 0 auto; \">\n");
      out.write("        <div style=\"width: 300px; height: 300px; margin: 120px 60px 0 auto; box-shadow: 1px 1px 5px #666;\">\n");
      out.write("            <div id=\"qr-login-box\" class=\"active\"\n");
      out.write("                 style=\"padding: 30px; height: 100%; background-color: #ecf6ff; color: black; box-sizing: border-box;position: relative;\">\n");
      out.write("                <!--登录方式提示-->\n");
      out.write("                <div class=\"login-tip\" style=\"position: absolute;top: 5px;right: 58px;\">\n");
      out.write("                    <div class=\"poptip\">\n");
      out.write("                        <div class=\"poptip-arrow\">\n");
      out.write("                            <em></em>\n");
      out.write("                            <span></span>\n");
      out.write("                        </div>\n");
      out.write("                        <div class=\"poptip-content\" style=\"color: #df9c1f; font-size: 12px; font-weight: 400\">\n");
      out.write("                            普通登录\n");
      out.write("                        </div>\n");
      out.write("                    </div>\n");
      out.write("                </div>\n");
      out.write("\n");
      out.write("                <!--切换登录方式-->\n");
      out.write("                <div class=\"login-switch\" style=\"position: absolute; top: 0; right: 0;\" onselectstart=\"return false;\">\n");
      out.write("                    <i id=\"pwd-login\" class=\"iconfont static\"></i>\n");
      out.write("                </div>\n");
      out.write("                <!--标题-->\n");
      out.write("                <div class=\"login-title\">\n");
      out.write("                    <span>扫码登录</span>\n");
      out.write("                </div>\n");
      out.write("\n");
      out.write("                <!--登录框区域-->\n");
      out.write("                <div style=\"width: 150px; height: 150px; top: 75px; left: 75px; position: absolute;\">\n");
      out.write("\n");
      out.write("                    <!--loading-->\n");
      out.write("                    <div id=\"qr-loading\"\n");
      out.write("                         style=\"position: absolute;top: 0; left: 0;width: 100%; height: 100%; background-color: #ecf6ff\">\n");
      out.write("                        <div class=\"\"\n");
      out.write("                             style=\"height: 100%; background: transparent  url(https://img.alicdn.com/tps/TB1R5zYKVXXXXb7XVXXXXXXXXXX-32-32.gif) no-repeat 50% 50%;\"></div>\n");
      out.write("\n");
      out.write("                    </div>\n");
      out.write("\n");
      out.write("                    <!--报错-->\n");
      out.write("                    <div id=\"qr-err\"\n");
      out.write("                         style=\"width: 100%; height: 100%; background-color: rgba(255,255,255,.9); position: absolute;top: 0; left: 0; \">\n");
      out.write("                        <div style=\"text-align: center;\">\n");
      out.write("                            <h6 id=\"qr-err-smg\" style=\"padding: 50px 0 10px;\">二维码已失效</h6>\n");
      out.write("                            <a href=\"javascript:;\" class=\"refresh J_QRCodeRefresh\" data-spm-anchor-id=\"a2107.1.0.0\">请点击刷新</a>\n");
      out.write("                        </div>\n");
      out.write("                    </div>\n");
      out.write("\n");
      out.write("                    <!--二维码-->\n");
      out.write("                    <div id=\"qr-box\"\n");
      out.write("                    style=\"background-color: white;position: absolute;top: 0; left: 0;padding: 10px; box-sizing: border-box;\">\n");
      out.write("                    </div>\n");
      out.write("                </div>\n");
      out.write("\n");
      out.write("                <!--扫码提示-->\n");
      out.write("                <div style=\"position: absolute; bottom: 20px; width: 100%; left: 65px;\" class=\"qrcode-desc\"\n");
      out.write("                     data-spm=\"25847036\">\n");
      out.write("                    <i class=\"iconfont\"></i>\n");
      out.write("\n");
      out.write("                    <p style=\"font-size: 12px; color: #6c6c6c; line-height: 35px;\"><font class=\"ft-gray\">打开 </font>\n");
      out.write("                        <a target=\"_blank\" class=\"light-link\">S盾</a>\n");
      out.write("                        <span class=\"ft-gray\">扫一扫登录</span>\n");
      out.write("                    </p>\n");
      out.write("\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            <div id=\"pwd-login-box\"\n");
      out.write("                 style=\"padding: 30px; height: 100%; background-color: #ecf6ff; color: black; box-sizing: border-box;position: relative;\">\n");
      out.write("                <!--登录方式切换提示-->\n");
      out.write("                <div class=\"login-tip\" style=\"position: absolute;top: 5px;right: 58px;\">\n");
      out.write("                    <div class=\"poptip\">\n");
      out.write("                        <div class=\"poptip-arrow\">\n");
      out.write("                            <em></em>\n");
      out.write("                            <span></span>\n");
      out.write("                        </div>\n");
      out.write("                        <div class=\"poptip-content\" style=\"color: #df9c1f; font-size: 12px; font-weight: 400\">\n");
      out.write("                            扫码登录\n");
      out.write("                        </div>\n");
      out.write("                    </div>\n");
      out.write("                </div>\n");
      out.write("\n");
      out.write("                <!--切换登录方式-->\n");
      out.write("                <div class=\"login-switch\" style=\"position: absolute; top: 0; right: 0;\" onselectstart=\"return false;\">\n");
      out.write("                    <i id=\"quick-login\" class=\"iconfont quick\"></i>\n");
      out.write("                </div>\n");
      out.write("                <!--标题-->\n");
      out.write("                <div class=\"login-title\" style=\"padding-top: 10px;\">\n");
      out.write("                    <span>普通登录</span>\n");
      out.write("                </div>\n");
      out.write("\n");
      out.write("                <!--登录框区域-->\n");
      out.write("                <div style=\"padding-top: 50px;\">\n");
      out.write("                    <form class=\"bs-example bs-example-form\" role=\"form\">\n");
      out.write("                        <div class=\"input-group\">\n");
      out.write("                            <span class=\"input-group-addon\"><i class=\"glyphicon glyphicon-user\"></i></span>\n");
      out.write("                            <input type=\"text\" class=\"form-control\" placeholder=\"请输入账号\">\n");
      out.write("                        </div><br>\n");
      out.write("\n");
      out.write("                        <div class=\"input-group\">\n");
      out.write("                            <span class=\"input-group-addon\"><i class=\"glyphicon glyphicon-lock\"></i></span>\n");
      out.write("                            <input type=\"password\" class=\"form-control\" placeholder=\"请输入密码\">\n");
      out.write("                        </div><br>\n");
      out.write("\n");
      out.write("                        <div class=\"input-group\" style=\"display: block;\">\n");
      out.write("                            <a class=\"btn btn-success form-control\">登录</a>\n");
      out.write("                        </div><br>\n");
      out.write("                    </form>\n");
      out.write("                </div>\n");
      out.write("\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("</div>\n");
      out.write("\n");
      out.write("\n");
      out.write("<div class=\"footer\" style=\"padding-top: 30px;\">\n");
      out.write("    <p class=\"text-center\">Copyright @ 2016\n");
      out.write("        <a href=\"http://www.people2000.net/\" title=\"众人科技\" target=\"_blank\">People2000 </a>\n");
      out.write("    </p>\n");
      out.write("</div>\n");
      out.write("    <script src='");
      out.print(request.getContextPath());
      out.write("/bower_components/jquery/dist/jquery.min.js'></script>\n");
      out.write("    <script src='");
      out.print(request.getContextPath());
      out.write("/bower_components/jquery_qrcode/jquery.qrcode.min.js'></script>\n");
      out.write("<script src=\"/js/login.js\"></script>\n");
      out.write("\n");
      out.write("</body>\n");
      out.write(" \n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
