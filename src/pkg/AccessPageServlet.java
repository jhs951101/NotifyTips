package pkg;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AccessPageServlet
 */
@WebServlet("/AccessPageServlet")
public class AccessPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccessPageServlet() {
        super();
    }
    
    public void showAlertAndMove(PrintWriter out, String msg){
    	out.println("<script>");
		out.println("alert('" + msg + "');");
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
		
		String pageName = request.getParameter("page");
		String resultCode;
		
		if(pageName.equals("registerTip")){
			RequestDispatcher dispatcher;
			
			if(LoginInfo.username == null)
				dispatcher = request.getRequestDispatcher("login.jsp");
			else
				dispatcher = request.getRequestDispatcher("registerTip.jsp");
			
			if(dispatcher != null)
				dispatcher.forward(request, response);
		}
		else if(pageName.equals("loadTips")){
			ReadHTMLCode readHTMLCode = new ReadHTMLCode();
			resultCode = readHTMLCode.readTextFile(this, request, "loadTips.txt");
			out.print(resultCode);
		}
		else if(pageName.equals("tipInfo")){
			ReadHTMLCode readHTMLCode = new ReadHTMLCode();
			resultCode = readHTMLCode.readTextFile(this, request, "tipInfo.txt");
			out.print(resultCode);
		}
		else if(pageName.equals("modifyUserInfo")){
			ReadHTMLCode readHTMLCode = new ReadHTMLCode();
			resultCode = readHTMLCode.readTextFile(this, request, "modifyUserInfo.txt");
			out.print(resultCode);
		}
		else{
			showAlertAndMove(out, "Error: 유효하지 않은 페이지입니다.");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
