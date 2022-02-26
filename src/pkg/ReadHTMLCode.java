package pkg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public class ReadHTMLCode {
	
	public String readTextFile(HttpServlet servlet, HttpServletRequest request, String filename){
		ServletContext context = servlet.getServletContext();
		InputStream is = context.getResourceAsStream("/resources/" + filename);
		String result = "";
        
        if (is != null) {
        	InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            
            try {
            	String str;
            	
            	String username = null;
            	String password = null;
            	String nickname = null;
            	String[] email = null;
            	String keywords = null;
            	
            	if(filename.equals("modifyUserInfo.txt")) {
            		DBOperation dbop = new DBOperation();
            		Success success = new Success();
            		
            		ResultSet resultSet = dbop.Select("SELECT * FROM MEMBER WHERE Username = '" + LoginInfo.username + "'", success);
            		
            		try {
            			AES256Util aes256 = new AES256Util("aes256-test-key!!");
						resultSet.next();
						
						username = resultSet.getString(1);
						password = aes256.aesDecode(resultSet.getString(2));
		            	nickname = resultSet.getString(3);
		            	email = resultSet.getString(4).split("@");
		            	
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
							| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
						e.printStackTrace();
					}
            		
            		resultSet = dbop.Select("SELECT Word FROM MEMBER m, USER_KEYWORDS uk WHERE m.Username = uk.Username AND m.Username = '" + LoginInfo.username + "'", success);
            		
            		try {
            			
            			if(resultSet.next())
            				keywords = resultSet.getString(1);
            			
						while(resultSet.next())
							keywords += (", " + resultSet.getString(1));
						
					} catch (SQLException e) {
						e.printStackTrace();
					}
            		
            		dbop.Exit();
            	}
            	
            	while ((str = reader.readLine()) != null){
            		
            		if(str.equals("<!--userInfo_username -->")){
            			result += username;
	            	}
            		else if(str.equals("<!--userInfo_password -->")){
            			result += password;
	            	}
            		else if(str.equals("<!--userInfo_nickname -->")){
            			result += nickname;
	            	}
            		else if(str.equals("<!--userInfo_email1 -->")){
            			result += email[0];
	            	}
            		else if(str.equals("<!--userInfo_email2 -->")){
            			result += email[1];
	            	}
            		else if(str.equals("<!--userInfo_keywords -->")){
            			result += keywords;
	            	}
            		else if(str.equals("<!--userInfo -->")){
            			if(LoginInfo.nickname == null)
            				result += "<a href='login.jsp'>로그인</a> <br><br>";
            			else
            				result += (LoginInfo.nickname + " <a href='/NotifyTips/AccessPageServlet?page=modifyUserInfo'>회원정보 수정</a> <a href='/NotifyTips/LogoutServlet'>로그아웃</a> <br><br>");
	            	}
            		else if(str.equals("<!--tipList -->")){
            			result += loadTipList(request);
	            	}
            		else if(str.equals("<!--tipInfo -->")){
            			result += loadTipInfo(request);
            		}
            		else if(str.equals("<!--evaluationScreen -->")) {
            			if(LoginInfo.username == null){
            				result += "<p align='center'>로그인 후에 평점 부여 가능합니다.</p>";
            			}
            			else {
            				result += ("<form name='evaluation' action='/NotifyTips/EvaluationServlet?tid="
	            						 + request.getParameter("tid")
	            						 + "' method='post' onsubmit='return Submit('evaluation');'>"
	            						 + "<p align='center'>"
	            						 + "★☆☆☆☆:<input type='radio' name='estimate' value='1'><br>"
	            						 + "★★☆☆☆:<input type='radio' name='estimate' value='2'><br>"
	            						 + "★★★☆☆:<input type='radio' name='estimate' value='3'><br>"
	            						 + "★★★★☆:<input type='radio' name='estimate' value='4'><br>"
	            						 + "★★★★★:<input type='radio' name='estimate' value='5'>"
	            						 + "<br><br>"		
	            						 + "<input type='submit' value='평가하기'>"
	            						 + "</p></form>");
            			}
            		}
            		else{
            			result += str;
            		}
    			}
    		      
	            reader.close();
	            isr.close();
	            is.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
        
		return result;
	}
	
	private String loadTipList(HttpServletRequest request){
		
		int pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		int numOfContents = LoginInfo.searchResults.size();
		String text = null;
			
			int numOfPages = numOfContents / 15;
			
			if(numOfContents % 15 != 0)
				numOfPages++;
			
			int i = 0;
			
			while(i < LoginInfo.searchResults.size() && i < 15 * (pageNumber-1)){
				i++;
			}
			
			text = "<table align='center' style='text-align:center; width:500px;'>";
			int a = 0;
			
			while(i < LoginInfo.searchResults.size() && a < 15){
				text += "<tr><td>";
					
				SearchResult sr = LoginInfo.searchResults.get(i);
				
				String tTitle = sr.Ttitle;
				int tId = sr.Tid;
				
				text += ("<p align='left'><a href='/NotifyTips/AccessPageServlet?page=tipInfo&tid=" + tId + "'>" + tTitle + "</a></p> </td></tr>");  // Tip 제목
				
				i++;
				a++;
			}
			
			text += "<tr><td> <br><br>";
			
			for(int j=1; j<=numOfPages; j++){
				if(j == pageNumber)
					text += (j + " ");
				else
					text += ("<a href='/NotifyTips/AccessPageServlet?page=loadTips&pageNumber=" + j + "'>" + j + "</a> ");
			}
			
			text += "</td></tr></table>";
		
		return text;
	}
	
	private String loadTipInfo(HttpServletRequest request){
		String text = null;
		
		DBOperation dbop = new DBOperation();
		Success success = new Success();
		
		ResultSet result = dbop.Select("Username, Ttitle, Content", "TIPS", "Tid=" + request.getParameter("tid"), "", "", "", success);
		
		try {
			if(result.next()){
				String username = result.getString(1);
				String ttitle = result.getString(2);
				String content = result.getString(3);
				
				text = "<table align='center'>"
						+ "<tr> <td style='width: 80px;'>Tip 제목: </td> <td style='width: 700px;'>" + ttitle + "</td></tr>"
						+ "<tr> <td style='width: 80px;'>등록자: </td> <td style='width: 700px;'>" + username + "</td></tr>"
						+ "<tr> <td style='width: 80px;'>내용: </td> <td style='width: 700px;'>" + content + "</td></tr> </table>";
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		dbop.Exit();
		
		return text;
	}
}