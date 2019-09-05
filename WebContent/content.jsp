<!-- 선택한 글의 내용을 보여주는 jsp 파일 -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="board.BoardDBBean" %>
<%@ page import="board.BoardDataBean" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ include file="color.jspf" %>    

<html>
<head>
<title>게시판</title>
<link href="style.css?ver=1" rel="stylesheet" type="text/css">
<script type="text/javascript">
  function down(filename){
     document.downForm.filename.value=filename;
     document.downForm.submit();
  }
</script>
</head>
<body bgcolor="<%=bodyback_c%>">

<form name=downForm action="download.jsp" method="post">
	<input type="hidden" name="filename">
</form>
<%
	int num = Integer.parseInt(request.getParameter("num"));
	String pageNum = request.getParameter("pageNum");
	
	SimpleDateFormat sdf =
			new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	try{
		BoardDBBean dbPro = BoardDBBean.getInstance();
		BoardDataBean article = dbPro.getArticle(num);
		
		int ref = article.getRef();							//35~37 article 레퍼런스를 사용해서 re,re_step,re_level 값을 얻어옴. 이는 답변글 작성시 이들 값을  답변글의 부모글로-
		int re_step = article.getRe_step();					//-정보를 넘겨주기 위해서임
		int re_level = article.getRe_level();
		String filename = article.getFilename();
		long filesize = article.getFilesize();
	%>
	
	<p>글내용 보기</p>
	
<form>
	<table>
		<tr height="30">																<!-- 34~59 num에 해당하는 글을 화면에 표시 -->
			<th align="center" width="125" bgcolor="<%=value_c %>">글번호</th>
			<td align="center" width="125" align="center">
				<%=article.getNum() %></td>
			<th align="center" width="125" bgcolor="<%=value_c %>">조회수</th>
			<td align="center" width="125" align="center">
				<%=article.getReadcount() %></td>
		</tr>
		<tr height="30">
			<th align="center" width="125" bgcolor="<%=value_c %>">작성자</th>
			<td align="center" width="125" align="center">
				<%=article.getWriter() %></td>
			<th align="center" width="125" bgcolor="<%=value_c %>">작성일</th>
			<td align="center" width="125" align="center">
				<%=sdf.format(article.getReg_date()) %></td>
		</tr>
		<tr height="30">
			<th align="center" width="125" bgcolor="<%=value_c %>">글제목</th>
			<td align="center" width="375" align="center" colspan="3">
				<%=article.getSubject() %></td>
		</tr>
		<tr height="30,auto">
			<th align="center" width="125" bgcolor="<%=value_c %>">글내용</th>
			<td align="left" width="375" colspan="3" style="word-break:break-all;">
				<pre style="word-wrap: break-word;white-space: pre-wrap;white-space: -moz-pre-wrap;white-space: -pre-wrap;white-space: -o-pre-wrap;word-break:break-all;"><%=article.getContent() %></pre></td>
		</tr>
		<tr height="30">
		  <th align="center" bgcolor="<%=value_c%>">첨부파일</th>
		  <td align="left" colspan="3" >
		  <% if(filename==null || filename.equals("")) { %>등록된 파일이 없습니다. 
			<% } else { %><a href="javascript:down('<%=filename%>')"><%=filename %></a>&nbsp;&nbsp;&nbsp;(<%=filesize %>bytes)
			<% } %>
		  </td>
		</tr>
		<tr height="30">															<!-- 68~82 글수정,삭제, 등 누르는 버튼들 -->
			<td colspan="4" bgcolor="<%=value_c %>" align="right">
				<input type="button" value="글수정"
				onclick="document.location.href='updateForm.jsp?num=<%=article.getNum() %>&pageNum=<%=pageNum%>'">
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" value="글삭제"
				onclick="document.location.href='deleteForm.jsp?num=<%=article.getNum() %>&pageNum=<%=pageNum%>'">
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" value="댓글쓰기"
				onclick="document.location.href='writeForm.jsp?num=<%=num %>&ref=<%=ref %>&re_step=<%=re_step %>&re_level=<%=re_level %>'">
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" value="글목록"
				onclick="document.location.href='list.jsp?pageNum=<%=pageNum%>'">
			</td>
		</tr>		
	</table>	
<%
	}catch(Exception e){}
%>
</form>
</body>
</html>




















