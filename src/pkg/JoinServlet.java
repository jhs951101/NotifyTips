package pkg;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class JoinServlet
 */
@WebServlet("/JoinServlet")
public class JoinServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JoinServlet() {
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
		String password = request.getParameter("password1");
		String nickname = request.getParameter("nickname");
		String email = request.getParameter("email1") + "@" + request.getParameter("email2");
		String keywords = request.getParameter("keywords");
		String peroid = request.getParameter("period");
		
		AES256Util aes256 = new AES256Util("aes256-test-key!!");
		String encodedPassword = null;
		
		try {
			encodedPassword = aes256.aesEncode(password);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		if(encodedPassword == null){
			showAlertAndMove(out, "Error: 비밀번호 암호화에 실패하여 회원가입이 진행되지 못하였습니다.", "join.jsp");
			return;
		}
		
		String values = "('" + username + "', '" + encodedPassword + "', '" + nickname + "', '" + email + "', " + peroid + ")";
		
		Success success = new Success();
		DBOperation dbop = new DBOperation();
		dbop.Insert("MEMBER", values, success);
		
		if(!success.successful){
			showAlertAndMove(out, "Error: 서버 상의 문제로 회원가입이 진행되지 못하였습니다.", "join.jsp");
			return;
		}
		
		String[] keywordArr = keywords.split(",");
		
		for(int i=0; i<keywordArr.length; i++){
			values = "('" + username + "', '" + keywordArr[i] + "')";
			dbop.Insert("USER_KEYWORDS", values, success);
			
			if(!success.successful)
				break;
		}
		
		dbop.Exit();
		
		if(!success.successful){
			showAlertAndMove(out, "Error: 서버 상의 문제로 회원가입이 진행되지 못하였습니다.", "join.jsp");
		}
		else{
			RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
			if(dispatcher != null){
				dispatcher.forward(request, response);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
