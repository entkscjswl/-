


/* 데이터베이스 테이블과 연동하여 작업하는 DB 처리빈 */
package board;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;
import javax.naming.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.sql.DataSource;

public class BoardDBBean {

		
	private static BoardDBBean instance = new BoardDBBean();
	// .jsp페이지에서 DB연동빈인 BoardDBBean 클래스 메소드에 접근시 필요함.
	public static BoardDBBean getInstance() {	//	getInstance() 메소드로 BoardDBBean 객체를 리턴.
		return instance;
	}
	
	private BoardDBBean() {}
	
	//커넥션 풀로부터 Conncetion 객체를 얻어냄
	private Connection getConnection() throws Exception{			//DB와의 연결을 위해 Connection 객체를 리턴
		Context initCtx = new InitialContext();
		Context envCtx = (Context)initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource)envCtx.lookup("jdbc/oracle");
		return ds.getConnection();
	}
	
	//Bboard 테이블에 글을 추가(writePro.jsp 페이지에서 사용)
	public void insertArticle(BoardDataBean article)
		throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int num = article.getNum();
		int ref = article.getRef();
		int re_step = article.getRe_step();
		int re_level = article.getRe_level();
		int number = 0;
		String sql = "";
		
		try {
			conn = getConnection();
			
			pstmt = conn.prepareStatement("select max(num) from bboard");
				rs = pstmt.executeQuery();
				
				if(rs.next())
					number = rs.getInt(1)+1;
				else
					number = 1;
				
				if(num!=0) {										//64~77 제목글과 답변글의 순서를 결정하는 작업
					sql = "update bboard set re_step=re_step+1 "
							+ " where ref=? and re_step>?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, ref);
					pstmt.setInt(2, re_step);
					pstmt.executeUpdate();
					re_step=re_step+1;
					re_level=re_level+1;
				}else {
					ref=number;
					re_step=0;
					re_level=0;
				}
				//쿼리를 작성
				sql = "insert into bboard (num,writer,email,subject,passwd,reg_date, "				//71~86 insert문을 사용하여 bboard테이블에 새로운 레코드를 추가하는 부분
						+ " ref, re_step,re_level,content,filename,filesize,ip) "
						+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";		//	*에러 수정*num은 나중에 추가함
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, number);
				pstmt.setString(2, article.getWriter());
				pstmt.setString(3, article.getEmail());
				pstmt.setString(4, article.getSubject());
				pstmt.setString(5, article.getPasswd());
				pstmt.setTimestamp(6, article.getReg_date());
				pstmt.setInt(7, ref);
				pstmt.setInt(8, re_step);
				pstmt.setInt(9, re_level);
				pstmt.setString(10, article.getContent());
				pstmt.setString(11,article.getFilename());
				pstmt.setLong(12,article.getFilesize());
				pstmt.setString(13, article.getIp());
				
				pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null)try {rs.close();} catch(SQLException e) {}
			if(pstmt != null)try {pstmt.close();} catch(SQLException e) {}
			if(conn != null)try {conn.close();} catch(SQLException e) {}
		}
	}
	
	//board 테이블에 저장된 전체 글의 수를 얻어냄(select문) // list.jsp에서 사용!
	public int getArticleCount()		//bboard 테이블에 저장되어 있는 전체 레코드의 수를 검새해 얻어냄.
		throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int x =0;
		
		try {
			conn = getConnection();
			
			pstmt = conn.prepareStatement("select count(*) from bboard");	//	전체 레코드의 수를 bboard에 질의하는 부분
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				x= rs.getInt(1);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null) try {rs.close();}catch(SQLException e) {}
			if(pstmt!=null) try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null) try {conn.close();}catch(SQLException e) {}
		}
		return x;															//	getArticleCount() 메소드를 호출 한 부분에서 x값(전체레코드)을 사용할 수 있게 함
	}
	//글의 목록 (복수개의 글)을 가져옴(select문) list.jsp에서 사용
	public List<BoardDataBean> getArticles(int start, int end)				//	start부터 end까지 개수만큼 레코드를 검색하는 메소드
		throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		List<BoardDataBean> articleList= null;								
		try {
			conn=getConnection();
			sql="select * from "				//	sql에서는 limit 대신 이렇게 써야함 ㅡㅡ 얼탱이 없음
					+ " (select ROWNUM rnum,num,writer,email,subject,passwd,reg_date,readcount,ref,re_step,re_level,content,ip "
					+ " from "
					+ " (select * from bboard order by ref desc, re_step asc) "
					+ " bboard) "
					+ " where rnum>=? and rnum<=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end+start-1);
			rs=pstmt.executeQuery();
			if(rs.next()) {													//	155~173 ArrayList에 board테이블에서 가져온 레코드를 하나씩 BoardDataBean 객체로 생성해-
				articleList = new ArrayList<BoardDataBean>(end);			//-ArrayList에 넣는다
				do {
					BoardDataBean article = new BoardDataBean();
					article.setNum(rs.getInt("num"));
					article.setWriter(rs.getString("writer"));
					article.setEmail(rs.getString("email"));
					article.setSubject(rs.getString("subject"));
					article.setPasswd(rs.getString("passwd"));
					article.setReg_date(rs.getTimestamp("reg_date"));
					article.setReadcount(rs.getInt("readcount"));
					article.setRef(rs.getInt("ref"));
					article.setRe_step(rs.getInt("re_step"));
					article.setRe_level(rs.getInt("re_level"));
					article.setContent(rs.getString("content"));
					article.setIp(rs.getString("ip"));
					
					articleList.add(article);								//	BoardDataBean 객체를 ArrayList에 넣는 부분
				}while(rs.next());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null) try {rs.close();}catch(SQLException e) {}
			if(pstmt!=null) try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null) try {conn.close();}catch(SQLException e) {}
		}
		return articleList;													//	list.jsp에서 이 ArrayList 객체에 접근해 글목록을 화면에 표시할 수 있게 됨.
	}
	//글의 내용 보기(1개) (select문) content.jsp에서 사용함
	public BoardDataBean getArticle(int num) throws Exception{				//	num에 해당하는 레코드만 검색
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardDataBean article = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("update bboard set readcount = readcount +1 where num = ?");	//181~183 글의 조회수를 1 증가시켜주는 부분
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
				
			pstmt = conn.prepareStatement(								//195~197 num에 해당하는 레코드를 검색해오는 부분
					"select * from bboard where num = ?");
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				article = new BoardDataBean();							//	BoardDataBean() 객체를 생성해 article에 값을 넘겨줌
				article.setNum(rs.getInt("num"));
				article.setWriter(rs.getString("writer"));
				article.setEmail(rs.getString("email"));
				article.setSubject(rs.getString("subject"));
				article.setPasswd(rs.getString("passwd"));
				article.setReg_date(rs.getTimestamp("reg_date"));
				article.setReadcount(rs.getInt("readcount"));
				article.setRef(rs.getInt("ref"));
				article.setRe_step(rs.getInt("re_step"));
				article.setRe_level(rs.getInt("re_level"));
				article.setContent(rs.getString("content"));
				article.setFilename(rs.getString("filename"));
				article.setFilesize(rs.getLong("filesize"));
				article.setIp(rs.getString("ip"));	
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null) try {rs.close();}catch(SQLException e) {}
			if(pstmt!=null) try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null) try {conn.close();}catch(SQLException e) {}
		}
		return article;															//	content.jsp에서 BoardDataBean()을 쓸 수 있도록 article을 리턴
	}
	
	
	//글 수정 폼에서 사용할 글의 내용(1개글) updateForm.jsp에서 사용
	public BoardDataBean updateGetArticle(int num) throws Exception{			//	num에 해당하는 레코드를 검색해옴(조회수를 증가시키는 부분 x)
		Connection conn = null;
		PreparedStatement pstmt= null;
		ResultSet rs = null;
		BoardDataBean article = null;
		try {
			conn = getConnection();
			
			pstmt=conn.prepareStatement(
					"select * from bboard where num = ?");
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				article = new BoardDataBean();
				article.setNum(rs.getInt("num"));
				article.setWriter(rs.getString("writer"));
				article.setEmail(rs.getString("email"));
				article.setSubject(rs.getString("subject"));
				article.setPasswd(rs.getString("passwd"));
				article.setReg_date(rs.getTimestamp("reg_date"));
				article.setReadcount(rs.getInt("readcount"));
				article.setRef(rs.getInt("ref"));
				article.setRe_step(rs.getInt("re_step"));
				article.setRe_level(rs.getInt("re_level"));
				article.setContent(rs.getString("content"));
				article.setIp(rs.getString("ip"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null) try {rs.close();}catch(SQLException e) {}
			if(pstmt!=null) try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null) try {conn.close();}catch(SQLException e) {}
		}
		return article;														//	updateForm.jsp에서 BoardDataBean()을 쓸 수 있도록 article을 리턴
	}
	

	//글수정처리에서 사용(update문) updatePro.jsp에서 사용
	public int updateArticle(BoardDataBean article) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String dbpasswd="";
		String sql = "";
		int x=-1;
		try {
			conn = getConnection();
			
			pstmt = conn.prepareStatement("select passwd from bboard where num = ?");		//268~271	수정할 글의 번호에 해당하는 비밀번호를 가져옴
			
			pstmt.setInt(1,article.getNum());
			rs = pstmt.executeQuery();
			
			if(rs.next()) {																	//273~291	수정할 글의 비밀번호가 사용자가 입력한 비밀번호와 같은지 비교해 글의 수정여부를 결정
				dbpasswd=rs.getString("passwd");
				if(dbpasswd.equals(article.getPasswd())) {
					sql="update bboard set writer=?,email=?,subject=?,passwd=?,content=? where num=?";	//276~287	비밀번호가 일치하면 수행
					
					pstmt = conn.prepareStatement(sql);
					
					pstmt.setString(1, article.getWriter());
					pstmt.setString(2, article.getEmail());
					pstmt.setString(3, article.getSubject());
					pstmt.setString(4, article.getPasswd());
					pstmt.setString(5, article.getContent());
					pstmt.setInt(6, article.getNum());
					pstmt.executeUpdate();
					x=1;
				}else {																					//288~289	비밀번호가 일치하지 않으면 수행
					x=0;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null) try {rs.close();}catch(SQLException e) {}
			if(pstmt!=null) try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null) try {conn.close();}catch(SQLException e) {}
		}
		return x;																			
	}

	
	//글 삭제 처리시에 사용(delete문) deletePro.jsp에서 사용
	public int deleteArticle(int num,String passwd) throws Exception{				//	num과 passwd 값을 가지고 글을 삭제하는 메소드
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String dbpasswd="";
		int x = -1;
		try {
			conn = getConnection();
			
			pstmt = conn.prepareStatement("select passwd from bboard where num = ?");	//313~316	사용자가 삭제할 글의 번호와 비밀번호를 가져오는 부분
			
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dbpasswd = rs.getString("passwd");
				if(dbpasswd.equals(passwd)) {											
					pstmt = conn.prepareStatement("delete from bboard where num = ?");	//321~325	비밀번호 일치 시 수행
					
					pstmt.setInt(1, num);
					pstmt.executeUpdate();
					x=1;//글삭제 성공
				}else																	//326~327	비밀번호 불일치시 수행
					x=0;//비밀번호 틀림
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null) try {rs.close();}catch(SQLException e) {}
			if(pstmt!=null) try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null) try {conn.close();}catch(SQLException e) {}
		}
		return x;
	}
	
	public static String replace(String str, String pattern, String replace) {
		int s = 0, e = 0;
		StringBuffer result = new StringBuffer();

		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}
	
	public static void delete(String s) {
		File file = new File(s);
		if (file.isFile()) {
			file.delete();
		}
	}
	
	public static String con(String s) {
		String str = null;
		try {
			str = new String(s.getBytes("8859_1"), "ksc5601");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	//파일 다운로드
	public void downLoad(HttpServletRequest req, HttpServletResponse res,
			JspWriter out, PageContext pageContext) {
	  String  SAVEFOLDER = "C:\\z_DooSanJAVA\\works\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\20190813_1\\FileUpload";
	  try {
	    String filename = req.getParameter("filename");
	    File file = new File(con(SAVEFOLDER + File.separator+ filename));
	    byte b[] = new byte[(int) file.length()];
	    res.setHeader("Accept-Ranges", "bytes");
	    String strClient = req.getHeader("User-Agent");
	    if (strClient.indexOf("MSIE6.0") != -1) {
	      res.setContentType("application/smnet;charset=euc-kr");
	      res.setHeader("Content-Disposition", "filename=" + filename + ";");
	    } else {
	      res.setContentType("application/smnet;charset=euc-kr");
	      res.setHeader("Content-Disposition", "attachment;filename="+ filename + ";");
	    }
	    out.clear();
	    out = pageContext.pushBody();
	    if (file.isFile()) {
	      BufferedInputStream fin = new BufferedInputStream(
	          new FileInputStream(file));
	      BufferedOutputStream outs = new BufferedOutputStream(
	          res.getOutputStream());
	      int read = 0;
	      while ((read = fin.read(b)) != -1) {
	        outs.write(b, 0, read);
	      }
	      outs.close();
	      fin.close();
	    }
	  } catch (Exception e) {
	    e.printStackTrace();
	  }
	}
}



























//
