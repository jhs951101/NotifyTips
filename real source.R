library(recommenderlab)

path = "C:/NotifyTips/evaluation.csv"

data<-read.csv(path,head=FALSE,sep=",")

r <- as(data, "realRatingMatrix")

numOfData = dim(r)[1]
traningData<- sample(numOfData, 1)
testList<-r[-traningData] 

keylist <- as(testList, "list")
keys <- names(keylist)

r_b <- binarize(r, minRating=1)

trainingSet<-r_b[traningData]
trainingSet<-trainingSet[rowCounts(trainingSet)>1]

model <- Recommender(trainingSet,method="IBCF",parameter ="Jaccard")
recommenderUserList<-r_b[-traningData] 

recommendList<-predict(model,recommenderUserList,n=5)

as(recommendList,"list")

keys
