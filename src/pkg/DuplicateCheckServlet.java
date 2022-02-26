package pkg;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UsernameCheckServlet
 */
@WebServlet("/DuplicateCheckServlet")
public class DuplicateCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DuplicateCheckServlet() {
        super();
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
		
		Success success = new Success();
		DBOperation dbop = new DBOperation();
		ResultSet result = dbop.Select("*", "MEMBER", "Username = '" + username + "'", "", "", "", success);
		
		String isduplicate = "error";  // isduplicate: 아이디 중복 여부
		
		if(success.successful){
			try {
				if(!result.next()){  // 아이디가 중복되지 않음
					isduplicate = "false";
				}
				else{
					isduplicate = "true";
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error: ResultSet Error");
			}
		}
		
		dbop.Exit();
		
        out.print(isduplicate);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
