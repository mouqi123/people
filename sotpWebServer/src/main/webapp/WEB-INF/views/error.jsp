<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String errorMsg = (String) request.getAttribute("errorMsg");
    response.getWriter().print(errorMsg);
//out.print(errorMsg);
%>