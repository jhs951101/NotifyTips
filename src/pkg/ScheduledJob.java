package pkg;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimerTask;

public class ScheduledJob extends TimerTask {
	
	public static long periodToSend = 86400000;
	public static long elapsedTime = 0;
	
	private HashMap<String, Integer[]> recommendInfo;
	private HashMap<String, Integer> recommendList;
	
	private int state;
	
	public ScheduledJob() {
		state = 1;
	}

	@Override
	public void run() {
		elapsedTime += periodToSend;
		
		if(state == 0)
			recommendTip2();
		else
			recommendTip1();
		
		state = 1-state;
	}
	
	public void recommendTip1() {
		System.out.println("Recommendation 1 Executed.");
		
		Success success = new Success();
		DBOperation dbop = new DBOperation();
		
		ResultSet result = dbop.Select("SELECT Username FROM MEMBER", success);
		ArrayList<String> users = new ArrayList<String>();
		
		try {
			while(result.next())
				users.add(result.getString(1));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		dbop.Reset();
		
		for(int i=0; i<users.size(); i++) {
			String username = users.get(i);
			String query = "SELECT e.Tid, AVG(e.Rate) as Rate" + 
					" FROM EVALUATION e" + 
					" WHERE e.Tid IN" + 
					" (SELECT t.Tid" + 
					" FROM MEMBER m, TIPS t, USER_KEYWORDS u, KEYWORDS k" + 
					" WHERE m.Username = u.Username" + 
					" AND t.Tid = k.Tid" + 
					" AND u.Word = k.Word" + 
					" AND m.Username = '" + username + "')" + 
					" GROUP BY e.Tid" + 
					" ORDER BY Rate DESC";
			
			ResultSet result1 = dbop.Select(query, success);
			
			if(success.successful) {
				int tid;
				int recommendTipId = -1;
				
				try {
					while(result1.next()) {
						tid = result1.getInt(1);
						
						DBOperation dbop2 = new DBOperation();
						result = dbop2.Select("SELECT Username FROM RECOMMENDED WHERE Username = '" + username + "' AND Tid = " + tid, success);
							
						try {
							if(!result.next()) {
								recommendTipId = tid;
								
								String values = "('" + username + "', " + recommendTipId + ")";
								dbop.Insert("RECOMMENDED", values, success);
								
								dbop2.Exit();
								break;
							}
									
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						dbop2.Exit();
					}
						
					if(recommendTipId != -1){
						System.out.println("Tip found!");
						dbop.Reset();
						result = dbop.Select("SELECT Nickname, Email FROM MEMBER WHERE Username = '" + username + "'", success);
						
						String nickname = null;
						String email = null;
						
						try {
							result.next();
							
							nickname = result.getString(1);
							email = result.getString(2);
								
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						dbop.Reset();
						result = dbop.Select("SELECT Username, Ttitle, Content FROM TIPS WHERE Tid = " + recommendTipId, success);
						
						String tipUsername = null;
						String title = null;
						String content = null;
						
						
						try {
							result.next();
								
							tipUsername = result.getString(1);
							title = result.getString(2);
							content = result.getString(3);
									
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						if(success.successful){
							System.out.println("Send Email!");
							
							// 이메일 전송
							SendEmail sendEnail = new SendEmail();
							String contentToSend = 
									"안녕하세요. 고객님이 좋아하실 만한 Tip을 하나 드립니다.\n\n"
										+ "제목: " + title + "\n"
										+ "작성자: " + tipUsername + "\n"
										+ "내용: " + content + "\n\n"
										+ "아래 링크에서 제공받은 Tip에 대해 평점을 줄 수 있습니다.\n"
										+ "http://localhost:8081/NotifyTips/AccessPageServlet?page=tipInfo&tid=" + recommendTipId;
							
							if(email != null)
								sendEnail.sendTo(email, "[Tip 추천] " + title, contentToSend);
						}
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			dbop.Reset();
		}
		
		dbop.Exit();
	}
	
	public void recommendTip2(){
		System.out.println("Recommendation 2 Executed.");
		
		Success success = new Success();
		DBOperation dbop = new DBOperation();
		Recommender recommender = new Recommender();
		
		recommender.saveCSVFile(dbop);  // csv 파일 저장
		
		recommendInfo = recommender.recommendTip(success);  // 추천 목록 get
		
		if(success.successful) {
			Iterator<String> it = recommendInfo.keySet().iterator();
			recommendList = new HashMap<String, Integer>();
			
			while(it.hasNext()) {
				String username = it.next();
				int period;
				
				ResultSet result = dbop.Select("SELECT Period FROM MEMBER WHERE Username = '" + username + "'", success);
				
				try {
					result.next();
					period = result.getInt(1);
					
					if(elapsedTime % period != 0) {
						dbop.Reset();
						continue;
					}
						
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				Integer[] products = recommendInfo.get(username);
				int recommendTipId = -1;
				
				if(products.length != 0) {
					dbop.Reset();
					
					for(int i=0; i<5; i++) {  // 중복 추천 여부 확인
						result = dbop.Select("SELECT Username FROM RECOMMENDED"
											+ " WHERE Username = '" + username + "' AND Tid = " + products[i], success);
						
						if(success.successful){
							try {
								if(!result.next()) {
									recommendTipId = products[i];
									dbop.Reset();
									break;
								}
									
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						
						dbop.Reset();
					}
					
					if(recommendTipId != -1){
						recommendList.put(username, recommendTipId);
						
						String values = "('" + username + "', " + recommendTipId + ")";
						dbop.Insert("RECOMMENDED", values, success);
					}
				}
				
				dbop.Reset();
			}
			
			it = recommendList.keySet().iterator();
			
			while(it.hasNext()){
				String username = it.next();
				ResultSet result = dbop.Select("SELECT Nickname, Email FROM MEMBER WHERE Username = '" + username + "'", success);
				
				String nickname = null;
				String email = null;
				
				if(success.successful){
					try {
						result.next();
						
						nickname = result.getString(1);
						email = result.getString(2);
							
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
					int tid = recommendList.get(username);
					
					dbop.Reset();
					result = dbop.Select("SELECT Username, Ttitle, Content FROM TIPS WHERE Tid = " + tid, success);
					
					String tipUsername = null;
					String title = null;
					String content = null;
					
					if(success.successful){
						try {
							result.next();
							
							tipUsername = result.getString(1);
							title = result.getString(2);
							content = result.getString(3);
								
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						System.out.println("Send Email!");
						
						// 이메일 전송
						SendEmail sendEnail = new SendEmail();
						String contentToSend = 
								"안녕하세요. " + nickname + "님, 고객님이 좋아하실 만한 Tip을 하나 드립니다.\n\n"
									+ "제목: " + title + "\n"
									+ "작성자: " + tipUsername + "\n"
									+ "내용: " + content + "\n\n"
									+ "아래 링크에서 제공받은 Tip에 대해 평점을 줄 수 있습니다.\n"
									+ "http://localhost:8081/NotifyTips/AccessPageServlet?page=tipInfo&tid=" + tid;
						
						if(email != null)
							sendEnail.sendTo(email, "[Tip 추천] " + title, contentToSend);
					}
				}
				
				dbop.Reset();
			}
		}
		
		dbop.Exit();
	}

}