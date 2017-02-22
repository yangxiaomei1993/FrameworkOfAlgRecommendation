/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RobustNessAnalysis;

import DecisionTree.J48;
import MetricExtaction.HistBasedCharacterization;
import fileUtil.fileHandle;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.StratifiedRemoveFolds;
import weka.filters.unsupervised.attribute.AddNoise;

/**
 *
 * @author String
 */
public class NumberOfInstancesAnalysis {
    
    public static int noiseRatio = 2;
    public static double sampleRatio = 95.0;
    
    public static Instances SampleInstances(Instances data, int randomSeed, double sampleRatio) throws Exception{
        Resample filter = new Resample();
        filter.setInputFormat(data);
        filter.setRandomSeed(randomSeed);
        filter.setNoReplacement(true);
        filter.setSampleSizePercent(sampleRatio);
        
        Instances sampData = Filter.useFilter(data, filter);
        return sampData;
    }
    
    public static Instances StratifiedRemoveInstances(Instances data, int numOfFolds) throws Exception{
        StratifiedRemoveFolds filter = new StratifiedRemoveFolds();
        filter.setFold(numOfFolds);
        filter.setFold(1);
        filter.setInvertSelection(true);
        filter.setInputFormat(data);
        filter.setSeed(1);
        Instances filterData = Filter.useFilter(data, filter);
        return filterData;
    }    
    
    public static Instances AddNoiseData(Instances data, int newPercent, int randomSeed, String index) throws Exception{
        AddNoise filter = new AddNoise();
        filter.setRandomSeed(randomSeed);
        filter.setPercent(newPercent);
        filter.setAttributeIndex(index);
        
        filter.setInputFormat(data);
        
        Instances filterData = Filter.useFilter(data, filter);
        return filterData;
    }
    
    public static void main(String[] args) throws IOException, Exception{
        String folderName = "E:\\My works\\Experiment results\\"
                + "Classification Algorithm Recommendation by Clustering\\"
                + "Data set\\Orig data sets\\";
        HistMetricsNoiseFolder(folderName);
        
//        String fileName = "E:\\My works\\Experiment results\\"
//                + "Classification Algorithm Recommendation by Clustering\\"
//                + "Data set\\Orig data sets\\tae.arff";
        
//        Instances data = readArffData(fileName);
//        HistMetrics(data);
//        HistMetricsNoise(data);
//        classificationPerformance(data);
        
//        classificationPerformanceNoise(data);
    }
    
    public static void HistMetricsFolder(String folderName) throws IOException, Exception{
        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(folderName, "arff");
        
        for (int i = 0; i < fileNames.length; i++) {
            if(i != 42){
            Instances data = readArffData(folderName + fileNames[i]);
//            System.err.println(i + " ===> " + fileNames[i]+"==========================");
            HistMetrics(data);
            }
        }
    }
    
    
    public static void HistMetricsNoiseFolder(String folderName) throws IOException, Exception{
        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(folderName, "arff");
        
        for (int i = 0; i < fileNames.length; i++) {
            if(i != 42){
            Instances data = readArffData(folderName + fileNames[i]);
//            System.err.println(i + " ===> " + fileNames[i]+"==========================");
            HistMetricsNoise(data);
            }
        }
    }    
    
    
    
    public static void HistMetrics(Instances data) throws Exception {
        HistBasedCharacterization HBC = new HistBasedCharacterization();
        HBC.setDataSet(data);
        HBC.CaculateMetrics();
        String metricLine = HBC.printMetrics();
        System.out.println(metricLine);
        double ratio = sampleRatio;
        int randomSeed = 1;
        for (int i = 1; i < 31; i++) {
            randomSeed = i;
            HBC = new HistBasedCharacterization();
            Instances sampleData = SampleInstances(data, randomSeed, ratio);
//            System.out.println(sampleData.numInstances());
            HBC.setDataSet(sampleData);
            HBC.CaculateMetrics();
            metricLine = HBC.printMetrics();
            System.out.println(metricLine);
//            ratio = ratio - 5;
        }
    }
    
    
    public static void HistMetricsNoise(Instances data) throws Exception {
        HistBasedCharacterization HBC = new HistBasedCharacterization();
        HBC.setDataSet(data);
        HBC.CaculateMetrics();
        String metricLine = HBC.printMetrics();
        System.out.println(metricLine);
        int ratio = noiseRatio;
        int randomSeed = 1;
        for (int i = 1; i < 51; i++) {
            randomSeed = i;
            Instances sampleData = AddNoiseData(data, ratio, randomSeed, "last");
            HBC = new HistBasedCharacterization();          
//            System.out.println(sampleData.numInstances());
            HBC.setDataSet(sampleData);
            HBC.CaculateMetrics();
            metricLine = HBC.printMetrics();
            System.out.println(metricLine);
//            ratio = ratio - 2;
        }
    }
    
    public static void classificationPerformance(Instances data) throws Exception{
//        SMO classifier = new SMO();
        J48 classifier = new J48();
        startClassification(classifier, data);
        double ratio = 95;
        int randomSeed = 1;
        for(int i = 1; i < 20; i++){
            randomSeed = i;
            Instances sampleData = SampleInstances(data, randomSeed, ratio);
            startClassification(classifier, sampleData);
//            System.out.println(sampleData.numInstances());
            ratio = ratio - 5;
        }         
    }
    
    public static void classificationPerformanceNoise(Instances data) throws Exception{
//        SMO classifier = new SMO();
        J48 classifier = new J48();
        startClassification(classifier, data);
        int ratio = noiseRatio;
        int randomSeed = 1;
        for(int i = 1; i < 51; i++){
            randomSeed = i;
             Instances sampleData = AddNoiseData(data, ratio, randomSeed, "last");
            startClassification(classifier, sampleData);
//            System.out.println(sampleData.numInstances());
//            ratio = ratio - 2;
        }         
    }    
    
    public static void startClassification(Classifier classifier, Instances data) throws Exception {
        double sum = 0;
        int runPasses = 1;
        for (int i = 1; i <= runPasses; i++) {
            Evaluation evaluation = new Evaluation(data);
            int randomSeed = i;
            Random rand = new Random(randomSeed);
            evaluation.crossValidateModel(classifier, data, 10, rand);
//        System.out.println(evaluation.correct()+"\t"+evaluation.toSummaryString());
//        System.out.println(evaluation.pctCorrect());
            sum = sum + evaluation.pctCorrect();
        }

        double acc = (sum / runPasses);

        System.out.println(acc);
    }
    
    public static Instances readArffData(String fileName) throws IOException {
        File file = new File(fileName);
        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setFile(file);

        Instances data = arffLoader.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }    
}
