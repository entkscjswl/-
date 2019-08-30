<!-- 글의 수정을 처리하는 페이지 -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="board.BoardDBBean" %>
<%@ page import = "java.sql.Timestamp" %>

<%request.setCharacterEncoding("utf-8"); %>

<jsp:useBean id="article" scope="page" class="board.BoardDataBean">		<!--8~10 updateform에서 작성한 수정글을 BoardDataBean 객체로 생성 -->
	<jsp:setProperty name ="article" property="*"/>
</jsp:useBean>
<%

	String pageNum = request.getParameter("pageNum");
	
	BoardDBBean dbPro = BoardDBBean.getInstance();
	int check = dbPro.updateArticle(article);		//	dbPro.updateArticle 메소드를 호출해 수정결과를 리턴받음. 리턴을 받은 check가 변수값이 1이상이면 수정이 성공, 0이면 글수정실패
	
	if(check==1){
%>
	<meta http-equiv="Refresh" content="0;url=list.jsp?pageNum=<%=pageNum%>">	<!-- 글 수정에 성공하면 list.jsp 페이지로 이동 -->
<%}else{%>
	<script type="text/javascript">
	<!-- 
		alert("비밀번호가 맞지 않습니다");
		history.go(-1);
	-->
	</script>
<%
}
%>