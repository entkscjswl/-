<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="board.*" %>
<%
BoardDBBean dbPro = BoardDBBean.getInstance();
dbPro.downLoad(request, response, out, pageContext);
%>
