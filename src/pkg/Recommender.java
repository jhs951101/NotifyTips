package pkg;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class Recommender {
	
	private final String PATH = "C:/NotifyTips/evaluation.csv";
	
	private HashMap<String, Integer[]> recommendInfo;
	
	public void saveCSVFile(DBOperation dbop) {
		File f = new File(PATH);
		
		if(f.exists())
			f.delete();
		
		Success success = new Success();
		
		dbop.Select("SELECT * INTO OUTFILE '" + PATH + "' FIELDS TERMINATED BY ',' FROM EVALUATION", success);
		dbop.Reset();
	}

	public HashMap<String, Integer[]> recommendTip(Success success) {
		Rengine engine = new Rengine(null, false, null);
		System.out.println("RJava is started.");
		
		if(!engine.waitForR()) {
			System.out.println("Cannot load R");
			success.successful = false;
			return null;
		}
		
		engine.eval("library(recommenderlab)", true);
		engine.eval("data <-read.csv(\"" + PATH + "\",head=FALSE,sep=\",\")", true);
		engine.eval("r <- as(data, \"realRatingMatrix\")", true);
		engine.eval("numOfData = dim(r)[1]", true);
		engine.eval("traningData<- sample(numOfData, 1)", true);
		engine.eval("testList<-r[-traningData] ", true);
		engine.eval("keylist <- as(testList, \"list\")", true);
		engine.eval("keys <- names(keylist)", true);
		engine.eval("r_b <- binarize(r, minRating=1)", true);
		engine.eval("trainingSet<-r_b[traningData]", true);
		engine.eval("trainingSet<-trainingSet[rowCounts(trainingSet)>1]", true);
		engine.eval("model <- Recommender(trainingSet,method=\"IBCF\",parameter =\"Jaccard\")", true);
		engine.eval("recommenderUserList<-r_b[-traningData] ", true);
		engine.eval("recommendList<-predict(model,recommenderUserList,n=5)", true);
		engine.eval("list2 <- as(recommendList,\"list\")", true);
		engine.eval("list1 <- keys", true);
		
		REXP x;
		
		x = engine.eval("list1", true);
		String arr1[] = x.asStringArray();
		
		x = engine.eval("length(list2)", true);
		
		int numOfUser = 0;
		numOfUser = x.asInt();
		
		if(numOfUser <= 0)
			numOfUser = 6;
		
		Integer[][] recommendList = new Integer[numOfUser][5];
		
		for(int i=1; i<=numOfUser; i++) {
			x = engine.eval("length(list2[[" + i + "]])", true);
			int numOfItem = x.asInt();
			
			if(numOfItem > 0) {
				for(int j=1; j<=5; j++) {
					x = engine.eval("as.integer(list2[[" + i + "]][" + j + "])", true);
					recommendList[i-1][j-1] = x.asInt();
				}
			}
			else {
				for(int j=1; j<=5; j++) {
					recommendList[i-1][j-1] = -1;
				}
			}
		}
		
		engine.end();
		
		recommendInfo = new HashMap<String, Integer[]>();
		
		for(int i=0; i<numOfUser; i++)
			recommendInfo.put(arr1[i], recommendList[i]);
		
		success.successful = true;
		
		System.out.println("Finish!");
		
		/*Iterator<String> it = recommendInfo.keySet().iterator();
		
		for(int i=0; i<numOfUser; i++) {
			String un = it.next();
			Integer[] is = recommendInfo.get(un);
			
			System.out.println(un);
			
			for(int j=0; j<is.length; j++) {
				System.out.print(is[j] + " ");
			}
			
			System.out.println();
		}*/
		
		return recommendInfo;
	}
}
