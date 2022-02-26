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
@WebServlet("/ModifyServlet")
public class ModifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModifyServlet() {
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
		
		String password = request.getParameter("password1");
		String nickname = request.getParameter("nickname");
		String email = request.getParameter("email1") + "@" + request.getParameter("email2");
		String keywords = request.getParameter("keywords");
		
		AES256Util aes256 = new AES256Util("aes256-test-key!!");
		String encodedPassword = null;
		
		try {
			encodedPassword = aes256.aesEncode(password);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		if(encodedPassword == null){
			showAlertAndMove(out, "Error: ��й�ȣ ��ȣȭ�� �����Ͽ� ȸ������ ������ ������� ���Ͽ����ϴ�.", "search.jsp");
			return;
		}
		
		String sets = "Password = '" + encodedPassword + "', Nickname = '" + nickname + "', Email = '" + email + "'";
		
		Success success = new Success();
		DBOperation dbop = new DBOperation();
		
		dbop.Update("MEMBER", sets, "Username = '" + LoginInfo.username + "'", success);
		
		if(!success.successful){
			showAlertAndMove(out, "Error: ���� ���� ������ ȸ������ ������ ������� ���Ͽ����ϴ�.", "search.jsp");
			return;
		}
		
		dbop.Delete("USER_KEYWORDS", "Username = '" + LoginInfo.username + "'", success);
		
		if(!success.successful){
			showAlertAndMove(out, "Error: ���� ���� ������ ȸ������ ������ ������� ���Ͽ����ϴ�.", "search.jsp");
			return;
		}
		
		String[] keywordArr = keywords.split(",");
		
		for(int i=0; i<keywordArr.length; i++){
			String values = "('" + LoginInfo.username + "', '" + keywordArr[i] + "')";
			dbop.Insert("USER_KEYWORDS", values, success);
			
			if(!success.successful)
				break;
		}
		
		dbop.Exit();
		
		if(!success.successful){
			showAlertAndMove(out, "Error: ���� ���� ������ ȸ������ ������ ������� ���Ͽ����ϴ�.", "search.jsp");
		}
		else{
			RequestDispatcher dispatcher = request.getRequestDispatcher("search.jsp");
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
