/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package learnAlgRecommendation;

import dataCharacteristics.dataProcess;
import fileUtil.fileHandle;
import fileUtil.fileOperator;
import weka.classifiers.Classifier;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;

/**
 *
 * @author String
 */
public class runOnDiffMachine {

    public static String dataFilePath;
    public static String savePath;

    public static void main(String[] args) throws Exception{
//        String dataFilePath = "E:\\My works\\Experiment results\\" +
//                "Frameworks of Algorithm Recommendation\\UCI Data Sets\\Classification\\";

        dataFilePath = args[0];
        savePath = args[1];

        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(dataFilePath, "arff", true);

//        NaiveBayes classifier = new NaiveBayes();
//        String classifierName = "Naive Bayes";
//        BayesNet classifier = new BayesNet();
//        String classifierName = "BayesNet";

//        IB1 classifier = new IB1();
//        String classifierName = "IB1";

//        J48 classifier = new J48();
//        String classifierName = "Decision Tree";

//        RandomTree classifier = new RandomTree();
//        String classifierName = "RandomTree";

          SimpleCart classifier = new SimpleCart();
          String classifierName = "CART";

//        JRip classifier = new JRip();
//        String classifierName = "JRip";

//        PART classifier = new PART();
//        String classifierName = "PART";

//        AdaBoostM1 classifier = new AdaBoostM1();
//        NaiveBayes subClassifer1 = new NaiveBayes();
//        classifier.setClassifier(subClassifer1);
//
//        String classifierName = "Boost+NB";

//        AdaBoostM1 classifier = new AdaBoostM1();
//        J48 subClassifier2 = new J48();
//        classifier.setClassifier(subClassifier2);
//
//        String classifierName = "Boost+J48";

//        AODE classifier = new AODE();
//        String classifierName = "AODE";

//         SMO classifier = new SMO();
//         String classifierName = "SMO";

//        RBFNetwork classifier = new RBFNetwork();
//        String classifierName = "RBFNet";

//        Bagging classifier = new Bagging();
//        NaiveBayes subClassifer1 = new NaiveBayes();
//        classifier.setClassifier(subClassifer1);
//
//        String classifierName = "Bag+NB";

//        Bagging classifier = new Bagging();
//        J48 subClassifier1 = new J48();
//        classifier.setClassifier(subClassifier1);
//
//        String classifierName = "Bag+J48";

        for(int i = 0; i < fileNames.length; i++){
            System.out.println((i+1)+"\t"+fileNames[i]);
            String fileName = fileNames[i];
            dataProcess dp = new dataProcess(dataFilePath+fileName+".arff");
            dp.readDataSet();

            Instances dataset = dp.getDataSet();
            if(dataset.numInstances() < 10){
                continue;
            }
            StringBuffer[] sb = runOneClassifier(dataset, classifier);
            saveOneClassifierResults(classifierName, fileName, sb);
        }
    }

    public static void saveOneClassifierResults(String classifierName, String fileName, StringBuffer[] results){

//        String savePath = "E:\\My works\\Experiment results\\Frameworks of Algorithm Recommendation" +
//                "\\Classification Accuracy and Runtime Oids\\";

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

        System.out.println(sb[0].toString());

        return sb;
    }


}
