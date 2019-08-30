<!-- 글의 삭제를 처리하는 페이지 -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "board.BoardDBBean"%>
<%@ page import = "java.sql.Timestamp"%>

<% request.setCharacterEncoding("utf-8");%>

<%
	int num = Integer.parseInt(request.getParameter("num"));		
	String pageNum = request.getParameter("pageNum");
	String passwd = request.getParameter("passwd");
	
	BoardDBBean dbPro = BoardDBBean.getInstance();
	int check = dbPro.deleteArticle(num,passwd);				//	글을 삭제하기 위해 BoardDBbean의 dbPro 레퍼런스를 이용 deleteArticle을 호출. 삭제할 글의 num과 passwd를 파라미터로 사용
	
	if(check==1){												//	check가 1이면 삭제 0이면 실패
%>
	<meta http-equiv="Refresh" content="0;url=list.jsp?pageNum=<%=pageNum%>">
<%}else{%>
	<script type="text/javascript">
		<!--
			alert("비밀번호가 맞지 않습니다.");
			history.go(-1);
		-->
	</script>
<%}%>