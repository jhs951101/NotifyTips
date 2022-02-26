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
 * Servlet implementation class EvaluationServlet
 */
@WebServlet("/EvaluationServlet")
public class EvaluationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EvaluationServlet() {
        super();
    }
    
    public void showAlert(PrintWriter out, String msg){
    	out.println("<script>");
		out.println("alert('" + msg + "');");
		out.println("</script>");
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
		
		String estimate = request.getParameter("estimate");
		String tid = request.getParameter("tid");
		
		String values = "('" + LoginInfo.username + "', " + tid + ", " + estimate + ")";
		
		Success success = new Success();
		DBOperation dbop = new DBOperation();
		
		ResultSet result = dbop.Select("SELECT * FROM EVALUATION WHERE Username = '" + LoginInfo.username + "' AND Tid = " + tid, success);
		boolean duplicate = true;
		
		if(success.successful){
			try {
				if(!result.next())
					duplicate = false;
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error: ResultSet Error");
			}
		}
		
		if(duplicate){
			dbop.Update("EVALUATION", "Rate = " + estimate, "Username = '" + LoginInfo.username + "' AND Tid = " + tid, success);
		}
		else {
			dbop.Insert("EVALUATION", values, success);
		}
		
		dbop.Reset();
		
		result = dbop.Select("SELECT Username FROM RECOMMENDED WHERE Username = '" + LoginInfo.username + "' AND Tid = " + tid, success);
			
		try {
			if(!result.next())
				dbop.Insert("RECOMMENDED", "('" + LoginInfo.username + "', " + tid + ")", success);
					
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		dbop.Exit();
		
		if(!success.successful){
			showAlertAndMove(out, "Error: 서버 상의 문제로 회원가입이 진행되지 못하였습니다.", "#");
		}
		else {
			showAlert(out, "정상적으로 처리 되었습니다.");
			out.flush();
			
			ReadHTMLCode readHTMLCode = new ReadHTMLCode();
			String resultCode = readHTMLCode.readTextFile(this, request, "tipInfo.txt");
			out.print(resultCode);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
