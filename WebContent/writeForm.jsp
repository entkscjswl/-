<!-- 게시판에 추가할 글을 입력하는 페이지 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="color.jspf" %>  
<html>
<head>
<title>게시판</title>
<link href="style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="script.js"></script>
</head>

<body bgcolor="<%=bodyback_c%>">
<%
int num=0,ref=1,re_step=0,re_level=0;
String strV="";
try{	
	if(request.getParameter("num")!=null){			//제목글과 답변글이 구별되도록 하는 부분. 17라인에서 num값이 null이 아니라면 이 글은 답변글이 됨.
		num=Integer.parseInt(request.getParameter("num"));				//만약 이글이 답변글이라면 18~21줄을 실행.
		ref=Integer.parseInt(request.getParameter("ref"));				
		re_step=Integer.parseInt(request.getParameter("re_step"));
		re_level=Integer.parseInt(request.getParameter("re_level"));
	}
%>
	<p>글쓰기</p>
	<form method="post" name="writeform" action="writePro.jsp" onsubmit="return writeSave()" enctype="multipart/form-data">
		<input type="hidden" name="num" value="<%=num %>">				<!-- 26~29 value들의 값은 writePro로 넘겨 자바빈의 insertArticle()에서 제목글과 답변글의 구분을 처리 -->
		<input type="hidden" name="ref" value="<%=ref %>">
		<input type="hidden" name="re_step" value="<%=re_step %>">
		<input type="hidden" name="re_level" value="<%=re_level %>">
	
	
	<table>
		<tr>
			<td align="right" colspan="2" bgcolor="<%=value_c %>">
				<a href="list.jsp">글목록</a>
			</td>
		</tr>
		<tr>
			<td width="70" bgcolor="<%=value_c %>" align="center">이름</td>
			<td width="330" align="left">
				<input type="text" size="10" maxlength="10"
				 name="writer" style="ime-mode:active;"></td>		<!--style 부분은 해당 입력란에 포커스가 들어오면 자동으로 한글로 입력되게 하는 역할-->
		</tr>
		<tr>
			<td width="70" bgcolor="<%=value_c %>" align="center">제목</td>
			<td width="330" align="left">
			<%
			if(request.getParameter("num")==null)				//제목글이면 48~49 실행
				strV="";
			else												//답변글이면 50~51 실행
				strV="[답변]";
			%>
			<input type="text" size="40" maxlength="50" name="subject"
			 value="<%=strV %>" style="ime-mode:active;"></td>
		</tr>
		<tr>
			<td width="70" bgcolor="<%=value_c %>" align="center">Email</td>
			<td width="330" align="left">
				<input type="text" size="40" maxlength="30" name="email"
				 style="ime-mode:inactive;"></td>		
		</tr>
		<tr>
			<td width="70" bgcolor="<%=value_c %>" align="center">내용</td>
			<td width="330" align="left">
				<textarea name="content" rows="13" cols="40"
				 style="ime-mode:active;"></textarea></td>
		</tr>
		<tr>
			<th bgcolor="<%=value_c%>">파일추가</th>
			<td align="left"><input type="file" name="filename">
		  </td>
		</tr>
		<tr>
			<td width="70" bgcolor="<%=value_c %>" align="center">비밀번호</td>
			<td width="330" align="left">
				<input type="password" size="8" maxlength="12"
				 name="passwd" style="ime-mode:inactive;"></td>	
		</tr>
		<tr>
			<td colspan="2" bgcolor="<%=value_c %>" align="center">
				<input type="submit" value="글쓰기">
				<input type="reset" value="다시 작성">
				<input type="button" value="목록보기" onClick="window.location='list.jsp'">
			</td>
		</tr>
	</table>

	</form>
<%
}catch(Exception e){}
%>
</body>
</html>

















