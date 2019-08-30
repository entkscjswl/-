<!--입력된 글을 넘겨받아 글 추가를 처리하는 페이지 --> 
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="board.*" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="com.oreilly.servlet.multipart.FileRenamePolicy" %>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.*" %>

<% request.setCharacterEncoding("UTF-8"); %>


<%
BoardDataBean article = new BoardDataBean();
String saveDirectory = application.getRealPath("/FileUpload");
int maxPostSize = 10 * 1024 * 1024;
String encoding = "utf-8";
FileRenamePolicy policy = new DefaultFileRenamePolicy();      
String fileName = "filename";

MultipartRequest multi = new MultipartRequest(request, saveDirectory, maxPostSize, encoding, policy);      
File file = multi.getFile(fileName);  // client의 파일경로+이름 즉 c:\직박구리\abc.jpg
if(file == null){
	  out.println("파일 추가 없음");
} else {
		fileName = file.getName();  // server 파일경로+이름 즉 /FileUpload/abc.jpg
		long fileSize = file.length();  // server들어가 있는 파일의 사이즈
		if(fileName == null){
		  out.println("파일 업로드 실패");
		}else{
		  article.setFilename(fileName);
		  article.setFilesize(fileSize);
		}
}
%>
<%
	article.setNum(Integer.parseInt(multi.getParameter("num")));
	article.setWriter(multi.getParameter("writer"));
	article.setContent(multi.getParameter("content"));
	article.setSubject(multi.getParameter("subject"));
	article.setPasswd(multi.getParameter("passwd"));
	article.setReg_date(new Timestamp(System.currentTimeMillis()));
	article.setIp(request.getRemoteAddr());
	
	BoardDBBean dbPro = BoardDBBean.getInstance();
	dbPro.insertArticle(article);
	
	response.sendRedirect("list.jsp");
%>