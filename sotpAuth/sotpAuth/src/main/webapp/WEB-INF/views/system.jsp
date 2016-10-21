<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         import="com.peopleNet.sotp.filter.RequestContextFilter,com.peopleNet.sotp.util.DateUtil, com.peopleNet.sotp.vo.InterfaceVisitStatistic"
         import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<%@ include file="/WEB-INF/views/global.jsp" %>
<html>
<head>
    <title>系统状态</title>
</head>
<body>
<div>
    <h3>系统运行状态</h3>
    <table style="text-align:center;border:1px solid">
        <thead>
        <tr>
            <th>系统路径</th>
            <th>启动时间</th>
            <th>运行时间(s)</th>
            <th>已用内存</th>
            <th>最大可用内存</th>
        </tr>
        </thead>
        <tr>
            <td style=" width:25%;"><%=request.getRealPath("")%>
            </td>
            <td><%=DateUtil.formatDate(RequestContextFilter.systemStartTime)%>
            </td>
            <td>
                <%
                    Date now = new Date();
                    long during = now.getTime() - RequestContextFilter.systemStartTime.getTime();
                    out.print(during / 1000);
                %>
            </td>
            <td><%=Runtime.getRuntime().totalMemory() / 1024 / 1024%>M</td>
            <td><%=Runtime.getRuntime().maxMemory() / 1024 / 1024%>M</td>
        </tr>
    </table>
</div>

<br/>
<br/>
<div>
    <h3>接口返回结果统计</h3>
    <table style="text-align:center;border:1px solid">
        <thead>
        <tr>
            <th>接口名称</th>
            <th>访问次数</th>
            <th>成功次数</th>
            <th>失败次数</th>
            <th>访问时间(ms)</th>
        </tr>
        </thead>

        <%
            for (Map.Entry<String, InterfaceVisitStatistic> entry : RequestContextFilter.interfaceVisitNum.entrySet()) {
        %>
        <tr>
            <td><%=entry.getKey()%>
            </td>
            <td><%=entry.getValue().getVisitNum()%>
            </td>
            <td><%=entry.getValue().getSuccessNum()%>
            </td>
            <td><%=entry.getValue().getFailNum()%>
            </td>
            <td><%=entry.getValue().getVisitTime()%>
            </td>
        </tr>
        <%
            }
        %>
    </table>
</div>

<br/>
<br/>
<div>
    <h3>接口访问时间统计</h3>
    <table style="text-align:center;border:1px solid">
        <thead>
        <tr>
            <th>接口名称</th>
            <th>总访问时间(ms)</th>
        </tr>
        </thead>

    </table>
</div>
</body>
</html>