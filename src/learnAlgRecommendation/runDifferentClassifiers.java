/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package learnAlgRecommendation;

import java.util.Arrays;
import java.util.Date;

import dataCharacteristics.dataProcess;
import fileUtil.fileHandle;
import fileUtil.fileOperator;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.AODE;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IB1;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.NNge;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;

/**
 *
 * @author String
 */
public class runDifferentClassifiers {

    public static String[] methodNames = {"AODE","NB","BayesNet","C4.5","CART","RandomTree",
    		"RandomForest","JRip","PART","OneR","NNge","MLP"
    		,"SMO", "IB1","RBFNet","Boost+NB","Boost+C4.5","Boost+PART","Bagging+NB","Bagging+C4.5","Bagging+PART"
    };

    protected static Classifier[] classifierSet;

    protected static void setClassifier(){
        classifierSet = new Classifier[methodNames.length];

//==============================================================================
        AODE classifier1 = new AODE();
        NaiveBayes classifier2 = new NaiveBayes();
        BayesNet classifier3 = new BayesNet();
        
//==============================================================================
        J48 classifier4 = new J48();
        SimpleCart classifier5 = new SimpleCart();
        RandomTree classifier6 = new RandomTree();
        RandomForest classifier7 = new RandomForest();//�¼�RandForest
        
//==============================================================================
        JRip classifier8 = new JRip();
        PART classifier9 = new PART();
        OneR classifier10 = new OneR();
        NNge classifier11 =new NNge();//�¼�NNge
        
//==============================================================================
        MultilayerPerceptron classifier12 = new MultilayerPerceptron();
        
//==============================================================================
        SMO classifier13 = new SMO();
        
//==============================================================================
        IB1 classifier14 = new IB1();

//==============================================================================
        RBFNetwork classifier15 = new RBFNetwork();

//==============================================================================
        AdaBoostM1 classifier16 = new AdaBoostM1();
        NaiveBayes subClassifer1 = new NaiveBayes();
        classifier16.setClassifier(subClassifer1); 
        
        AdaBoostM1 classifier17 = new AdaBoostM1();
        J48 subClassifier2 = new J48();
        classifier17.setClassifier(subClassifier2);
        
        AdaBoostM1 classifier18 = new AdaBoostM1();
        PART subClassifier3 = new PART();
        classifier18.setClassifier(subClassifier3);//�¼�Boosting+PART
        
        Bagging classifier19 = new Bagging();
        NaiveBayes subClassifier4 =new NaiveBayes();
        classifier19.setClassifier(subClassifier4);//�¼�Bagging+NaiveBayes
        
        Bagging classifier20 = new Bagging();
        J48 subClassifier5 =new J48();
        classifier20.setClassifier(subClassifier5);//�¼�Bagging+J48
        
        Bagging classifier21 = new Bagging();
        PART subClassifier6 =new PART();
        classifier21.setClassifier(subClassifier6);

//==============================================================================
        
        classifierSet[0] = classifier1;
        classifierSet[1] = classifier2;
        classifierSet[2] = classifier3;
        classifierSet[3] = classifier4;
        classifierSet[4] = classifier5;
        classifierSet[5] = classifier6;
        classifierSet[6] = classifier7;
        classifierSet[7] = classifier8;
        classifierSet[8] = classifier9;
        classifierSet[9] = classifier10;
        classifierSet[10] = classifier11;
        classifierSet[11] = classifier12;
        classifierSet[12] = classifier13;
        classifierSet[13] = classifier14;
        classifierSet[14] = classifier15;
        classifierSet[15] = classifier16;
        classifierSet[16] = classifier17;
        classifierSet[17] = classifier18;
        classifierSet[18] = classifier19;
        classifierSet[19] = classifier20;
        classifierSet[20] = classifier21;
        
    }

/*    public static void main(String[] args) throws Exception{

        String dataFilePath = "E:\\experiment\\dataset\\UCI\\dataSetOids\\";

        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(dataFilePath, "arff", true);


        int count = 0;
        for(int i = 0; i < fileNames.length; i++){
            System.out.println((i+1)+"\t"+fileNames[i]);
            String fileName = fileNames[i];
            dataProcess dp = new dataProcess(dataFilePath+fileName+".arff");
            dp.readDataSet();

            Instances dataset = dp.getDataSet();
            if(dataset.numInstances() < 10){
                continue;
            }
            count++;
//            StringBuffer[] sb = runOneClassifier(dataset, classifier);
//            saveOneClassifierResults(classifierName, fileName, sb);
        }

        System.out.println(count + "\t" + fileNames.length);
    }*/

    public static void saveOneClassifierResults(String classifierName, String fileName, StringBuffer[] results){

//        String savePath = "E:\\My works\\Experiment results\\Frameworks of Algorithm Recommendation" +
//                "\\Classification Accuracy and Runtime Oids\\";
//        String savePath = "E:\\experiment\\result\\Classification Accuracy and Runtime\\origin\\";

        fileOperator fo = new fileOperator();
        fo.openWriteFile(savePath + classifierName + "\\" + fileName + "_Acc.txt");
        fo.writeFile(results[0].toString());
        fo.closeWriteFile();
        System.out.flush();

        fo.openWriteFile(savePath + classifierName + "\\" + fileName + "_Time.txt");
        fo.writeFile(results[1].toString());
        fo.closeWriteFile();
        System.out.flush();
    }

    protected static StringBuffer[] runOneClassifier(Instances dataset, Classifier classifier) throws Exception{

        int passNum = 5;
        int foldNum = 10;

        MyCrossValidation MCV = new MyCrossValidation(dataset);
        MCV.setClassifier(classifier);
        MCV.MXNCrossValidation(passNum, foldNum);

        double[] acc = MCV.getMetricsSole();
        double[] time = MCV.getRuntimesSole();

        StringBuffer sb[] = new StringBuffer[2];

        sb[0] = new StringBuffer();
        sb[1] = new StringBuffer();

        for(int i = 0; i < acc.length; i++){
            sb[0].append(acc[i]+",");
            sb[1].append(time[i]+",");
        }
        sb[0].append("\n");
        sb[1].append("\n");

        //System.out.println(sb[0].toString());
        
        return sb;
    }

    protected static StringBuffer[] runOnOneDataset(Instances dataset) throws Exception{
        
        int passNum = 5;
        int foldNum = 10;

        int[] runFirstMethIndexes = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};

        double[][] accuracys = new double[runFirstMethIndexes.length][];
        double[][] runtimes = new double[runFirstMethIndexes.length][];

        for (int i = 0; i < runFirstMethIndexes.length; i++) {
        	System.out.println("classifier"+(i+1)+"\t"+methodNames[runFirstMethIndexes[i]].toString());
            MyCrossValidation MCV = new MyCrossValidation(dataset);
            ClassifierThreadKill classkill=new ClassifierThreadKill(MCV,dataset,classifierSet[runFirstMethIndexes[i]],passNum,foldNum);
//            MCV.setClassifier(classifierSet[runFirstMethIndexes[i]]);
//            MCV.MXNCrossValidation(passNum, foldNum);
            Thread thread=new Thread(classkill);
            Date dateStart=new Date();
            thread.start();
            while (true) {
				if (classkill.isFinished())
				{
					break;
				}
				else {
					Date dateCurrent=new Date();
					if (dateCurrent.getHours()-dateStart.getHours()>1) {
						//时间
						System.out.println("\t"+"kill");
						thread.stop();
						break;
					}
					Thread.sleep(1000);
				}
				
			}
 
            double[] accs = classkill.getAcc();
            double[] times = classkill.getTimes();
            accuracys[i] = accs;
            runtimes[i] = times;        
        }

        StringBuffer sb[] = new StringBuffer[2];

        sb[0] = new StringBuffer();
        sb[1] = new StringBuffer();

        for(int i = 0; i < accuracys.length; i++){
            for(int j = 0; j < accuracys[i].length; j++){
                sb[0].append(accuracys[i][j]+",");
                sb[1].append(runtimes[i][j]+",");
                System.out.print(accuracys[i][j]+",");
            }
            sb[0].append("\n");
            sb[1].append("\n");
            System.out.println();
        }
        return sb;
    }

    public static void saveResults(String fileName, StringBuffer[] results){
//      String savePath = "E:\\My works\\Experiment results\\Algorithm " +
//              "Recommendation\\Classification Results\\";

      String savePath = "E:\\experiment\\result\\Classification Accuracy and Runtime\\oids\\";

      fileOperator fo = new fileOperator();
      fo.openWriteFile(savePath+"accuracy\\"+fileName+".txt");
      fo.writeFile(results[0].toString());
      fo.closeWriteFile();
      System.out.flush();
      

      fo.openWriteFile(savePath+"runtime\\"+fileName+".txt");
      fo.writeFile(results[1].toString());
      fo.closeWriteFile();
      System.out.flush();
  }

  public static String[] getFileNames(String filePath){
      fileHandle fh = new fileHandle();
      return fh.getFileNames(filePath, "arff",true);
  }


    public static void main(String []args) throws Exception{
//������������ݵ����������
        String datafilePath = "E:\\experiment\\dataset\\UCI\\dataSetOids\\";
        String fileNames[] = getFileNames(datafilePath);

        setClassifier();

        System.out.println(fileNames.length);

       // int dataIndexes[] = {54,55,56,57};
        for(int i = 0; i < fileNames.length; i++){
            System.out.println((i+1)+"\t"+fileNames[i]);
            //String fileName = fileNames[dataIndexes[i]];
            dataProcess dp = new dataProcess(datafilePath+fileNames[i]+".arff");
            dp.readDataSet();

            Instances dataset = dp.getDataSet();
            StringBuffer[] sb = runOnOneDataset(dataset);
//            System.out.println(sb[0].toString());
//            System.out.println(sb[1].toString());
            saveResults(fileNames[i]+".arff", sb);
        }
    }
}
    