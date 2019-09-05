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
File file = multi.getFile(fileName);  
if(file == null){
	  out.println("파일 추가 없음");
} else {
		fileName = file.getName();  
		long fileSize = file.length();  
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
	article.setNum(Integer.parseInt(multi.getParameter("num")));
	article.setRef(Integer.parseInt(multi.getParameter("ref")));
	article.setRe_step(Integer.parseInt(multi.getParameter("re_step")));
	article.setRe_level(Integer.parseInt(multi.getParameter("re_level")));
	article.setReg_date(new Timestamp(System.currentTimeMillis()));
	article.setIp(request.getRemoteAddr());
	
	BoardDBBean dbPro = BoardDBBean.getInstance();
	dbPro.insertArticle(article);
	
	response.sendRedirect("list.jsp");
%>