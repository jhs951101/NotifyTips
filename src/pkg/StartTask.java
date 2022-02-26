package pkg;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
 
@WebListener
public class StartTask implements ServletContextListener {
 
	private Timer jobScheduler;
	
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
    	ScheduledJob job = new ScheduledJob();
        jobScheduler = new Timer();
        System.out.println("Web Server is started.");
        
        jobScheduler.scheduleAtFixedRate(job, 120000, ScheduledJob.periodToSend);  // 86,400,000 ms - 1¿œ
    }
 
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    	//jobScheduler.cancel();
    }
 
}