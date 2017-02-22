/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package learnAlgRecommendation;

import dataCharacteristics.dataProcess;
import dataCharacteristics.newMetrics;
import weka.classifiers.bayes.AODE;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.NNge;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 *
 * @author String
 */
public class learningTest {

    public static void main(String[] args) throws Exception{
        String fileName = "E:\\experiment\\dataset\\UCI\\arff\\anneal.arff";
        dataProcess dp = new dataProcess(fileName);
        dp.readDataSet();

        Instances dataset = dp.getDataSet();
        
//≤‚ ‘NaiveBayes
        //NaiveBayes classifier = new NaiveBayes();  
        
//≤‚ ‘NNge        
       // NNge classifier =new NNge();
        
//≤‚ ‘Boosting+PART
        //AdaBoostM1 classifier = new AdaBoostM1();
        //PART subClassifier = new PART();
        //classifier.setClassifier(subClassifier);
       
//≤‚ ‘Bagging+NaiveBayes        
        //Bagging classifier = new Bagging();
        //NaiveBayes subClassifier =new NaiveBayes();
        //classifier.setClassifier(subClassifier);       
        
//≤‚ ‘Bagging+J48
        //Bagging classifier = new Bagging();
        //J48 subClassifier =new J48();
        //classifier.setClassifier(subClassifier);
        
//≤‚ ‘Bagging+PART
       // Bagging classifier = new Bagging();
       // PART subClassifier =new PART();
       // classifier.setClassifier(subClassifier);
        
//≤‚ ‘MultilayerPerceptron
       // MultilayerPerceptron classifier = new MultilayerPerceptron();

//≤‚ ‘AODE
        AODE classifier = new AODE();
                
        MyCrossValidation MCV = new MyCrossValidation(dataset);
        MCV.setClassifier(classifier);

        MCV.MXNCrossValidation(5, 10);

        double accs[] = MCV.getMetricsSole();
        for(int i = 0; i < accs.length; i++){
                System.out.println(accs[i]);
        }
    }
}
