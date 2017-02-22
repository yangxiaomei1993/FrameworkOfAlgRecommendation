package learnAlgRecommendation;

import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.naming.spi.DirStateFactory.Result;

import dataCharacteristics.dataProcess;
import fileUtil.fileHandle;
import mulan.evaluation.measure.MeanAveragePrecision;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

/** 
* @author  Yang Xiaomei 
* @date 2016年9月26日 上午9:23:54 
* @description  this program is for paper “Single classifier selection for ensemble learning”
* 在十折交叉验证中，训练集部分随机抽样50%，测试集不变
*/
public class randomForestforPaper {

	public randomForestforPaper() {
		// TODO Auto-generated constructor stub
	}
	  public static String[] getFileNames(String filePath){
	      fileHandle fh = new fileHandle();
	      return fh.getFileNames(filePath, "arff",true);
	  }

	public static void main(String[] args) throws Exception {
		
		FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\result\\adaboost_DT\\adaboost_DT70.csv",true);
		PrintStream ps = new PrintStream(out);
		System.setOut(ps); //保存结果
		
//		RandomForest classifier = new RandomForest();
//		classifier.setNumTrees(70);
		AdaBoostM1 classifier = new AdaBoostM1();
        J48 subClassifier = new J48();
        classifier.setClassifier(subClassifier);
        classifier.setNumIterations(70);
		
		String datafilePath = "C:\\Users\\Administrator\\Desktop\\DataforPaper\\";
        String fileNames[] = getFileNames(datafilePath);
        
        runDifferentClassifiers rd = new runDifferentClassifiers();
        
        for (int j = 0; j < fileNames.length;j++)
        {
            dataProcess dp = new dataProcess(datafilePath+fileNames[j]+".arff");
            dp.readDataSet();

            Instances dataset = dp.getDataSet();
            
            StringBuffer[] sb = rd.runOneClassifier(dataset, classifier);
            System.out.println((j+1) + "," +fileNames[j] + "," + sb[0].toString());
        	
        }
		
		
		
		// TODO Auto-generated method stub

	}

}
