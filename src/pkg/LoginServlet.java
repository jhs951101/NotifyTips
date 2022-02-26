package pkg;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }
    
    public void showAlertAndMove(PrintWriter out, String msg, String page){
    	out.println("<script>");
		out.println("alert('" + msg + "');");
		out.println("location.href = '" + page + "';");
		out.println("</script>");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = response.getWriter();
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Success success = new Success();
		DBOperation dbop = new DBOperation();
		ResultSet result = dbop.Select("Password, Nickname", "MEMBER", "Username = '" + username + "'", "", "", "", success);
		
		boolean correct = false;
		
		if(success.successful){
			
			String decodedPassword = null;
			
			try {
				
				if(result.next()){  // 아이디 존재 여부
					AES256Util aes256 = new AES256Util("aes256-test-key!!");
					
					try {
						decodedPassword = aes256.aesDecode(result.getString(1));
					} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
							| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
						e.printStackTrace();
					}
					
					if(decodedPassword == null){
						showAlertAndMove(out, "Error: 복호화 과정 속에서 오류가 생겼습니다.", "login.jsp");
					}
					else{
						if(!decodedPassword.equals(password)){  // 비밀번호 불일치
							showAlertAndMove(out, "비밀번호가 일치하지 않습니다.", "login.jsp");
						}
						else{  // 아이디, 비밀번호 모두 일치
							LoginInfo.username = username;
							LoginInfo.nickname = result.getString(2);
							correct = true;
						}
					}
					
				}
				else{
					showAlertAndMove(out, "존재하지 않는 아이디입니다.", "login.jsp");
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error: ResultSet Error");
				showAlertAndMove(out, "Error: 서버 상의 오류로 로그인이 진행되지 못하였습니다.", "login.jsp");
			}
		}
		else {
			showAlertAndMove(out, "Error: 서버 상의 오류로 로그인이 진행되지 못하였습니다.", "login.jsp");
		}
		
		dbop.Exit();
		
		if(correct){
			/*RequestDispatcher dispatcher = request.getRequestDispatcher("search.jsp");
			if(dispatcher != null){
				dispatcher.forward(request, response);
			}*/
			
			showAlertAndMove(out, username + "님, 환영합니다!", "search.jsp");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}
