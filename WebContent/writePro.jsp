<!--입력된 글을 넘겨받아 글 추가를 처리하는 페이지 --> 
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="board.BoardDBBean" %>
<%@ page import="java.sql.Timestamp" %>

<% request.setCharacterEncoding("UTF-8"); %>

<jsp:useBean id="article" scope="page" class="board.BoardDataBean">	<!-- writeForm.jsp에서 입력한 글을 가지고 BoardDataBean 클래스의 객체 article을 생성 -->
	<jsp:setProperty name="article" property="*"/>
</jsp:useBean>

<%
	article.setReg_date(new Timestamp(System.currentTimeMillis()));
	article.setIp(request.getRemoteAddr());
	
	BoardDBBean dbPro = BoardDBBean.getInstance();
	dbPro.insertArticle(article);
	
	response.sendRedirect("list.jsp");
%>