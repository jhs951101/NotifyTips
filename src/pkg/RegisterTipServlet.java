package pkg;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegisterTipServlet
 */
@WebServlet("/RegisterTipServlet")
public class RegisterTipServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterTipServlet() {
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
		
		String title = request.getParameter("title");
		String keyword = request.getParameter("keyword");
		String explain = request.getParameter("explain");
		
		String values = "(null, '" + LoginInfo.username + "', '" + title + "', '" + explain + "')";
		
		Success success = new Success();
		DBOperation dbop = new DBOperation();
		
		dbop.Insert("TIPS", values, success);
		dbop.Exit();
		
		if(!success.successful){
			showAlertAndMove(out, "Error: 서버 상의 오류로 로그인이 진행되지 못하였습니다.", "registerTip.jsp");
			return;
		}
		
		try {
			ResultSet result = dbop.Select("MAX(Tid)", "TIPS", "", "", "", "", success);
			result.next();
			
			int maxId = result.getInt(1);
			
			String[] keywords = keyword.split(",");
			
			for(int i=0; i<keywords.length; i++){
				values = "(" + maxId + ", '" + keywords[i] + "')";
				dbop.Insert("KEYWORDS", values, success);
				
				if(!success.successful)
					break;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			showAlertAndMove(out, "Error: 서버 상의 오류로 로그인이 진행되지 못하였습니다.", "registerTip.jsp");
			return;
		}
		
		if(!success.successful){
			showAlertAndMove(out, "Error: 서버 상의 오류로 로그인이 진행되지 못하였습니다.", "registerTip.jsp");
			return;
		}
		
		showAlertAndMove(out, "정상적으로 진행 되었습니다.", "registerTip.jsp");
		
		dbop.Exit();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
