package pkg;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private ArrayList<String[]> prefixs;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
    }
    
    public void subset_r(String[] prefix, String[] s, int start, int end) {
    	if(start <= end){
    		int preLength;
    		
    		if(prefix == null)
    			preLength = 0;
    		else
    			preLength = prefix.length;
    		
    		String[] arr = new String[preLength + 1];
    		
    		for(int i=0; i<preLength; i++)
    			arr[i] = prefix[i];
    		
    		arr[preLength] = s[start];
    		
    		subset_r(arr, s, start+1, end);
    		subset_r(prefix, s, start+1, end);
    	}
    	else {
    		if(prefix != null){
    			if(prefix.length > 0){
    				prefixs.add(prefix);
    			}
    		}
    	}
    }

    public void subset(String[] s){
    	subset_r(null, s, 0, s.length-1);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = response.getWriter();
		
		String searchString = request.getParameter("inputSearch");
		String[] strarr = searchString.split(" ");
		prefixs = new ArrayList<String[]>();
		
		subset(strarr);
		
		String[] arr = prefixs.get(0);
		String query = "(SELECT Ttitle, Tid FROM TIPS WHERE Ttitle like '%" + arr[0] + "%'";
		
		for(int j=1; j<arr.length; j++)
			query += (" AND Ttitle like '%" + arr[j] + "%'");
		
		query += ")";
		
		for(int i=1; i<prefixs.size(); i++){
    		arr = prefixs.get(i);
    			
    		query += " UNION (SELECT Ttitle, Tid FROM TIPS WHERE Ttitle like '%" + arr[0] + "%'";
    		
    		for(int j=1; j<arr.length; j++)
    			query += (" AND Ttitle like '%" + arr[j] + "%'");
    		
    		query += ")";
    	}
		
		DBOperation dbop = new DBOperation();
		Success success = new Success();
		
		ResultSet resultSet = dbop.Select(query, success);
		
		LoginInfo.searchResults = new ArrayList<SearchResult>();
		
		try {
			while(resultSet.next()){
				LoginInfo.searchResults.add( new SearchResult(resultSet.getInt(2), resultSet.getString(1)) );
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ReadHTMLCode readHTMLCode = new ReadHTMLCode();
		String resultCode = readHTMLCode.readTextFile(this, request, "loadTips.txt");
		
		dbop.Exit();
		
		out.print(resultCode);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
