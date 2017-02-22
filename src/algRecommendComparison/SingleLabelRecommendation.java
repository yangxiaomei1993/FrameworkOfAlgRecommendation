/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algRecommendComparison;

import DecisionTree.J48;
import fileUtil.fileOperator;
import java.io.File;
import java.io.IOException;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * Leave one out prediction
 * @author String
 */
public class SingleLabelRecommendation {

    public final static String[] dataFileNames = {"complexBased_single","freqAnd_single",
    "freqXor_single", "landMark_single", "mixAnd_single", "mixXor_single", 
    "modelBased_single", "tradMetricsBased_single"};
    public final static String[] saveFileNames = {"complex", "freqAnd", "freqXor",
    "landMark", "mixAnd", "mixXor", "modelBased", "tradMetrics"};
    public final static String singleDataPath = "E:\\My works\\Experiment results" +
            "\\Frameworks of Algorithm Recommendation\\" +
            "Arff meta datasets\\Single Labels\\";
    public final static String singleSavePath = "E:\\My works\\Experiment results" +
            "\\Frameworks of Algorithm Recommendation\\Arff meta datasets\\Single Results\\";

    public static double[] LeaveOneOutRecommendation(Classifier classifier, Instances data) throws Exception{
        
        double[] predArray = new double[data.numInstances()];
        double acc = 0;
        for(int i = 0; i < predArray.length; i++){
            Instance insi = data.instance(i);
            Instances train = new Instances(data, 0, data.numInstances());
            train.delete(i);

            Evaluation evaluator = new Evaluation(train);
            Classifier copiedClassifier = Classifier.makeCopy(classifier);
            copiedClassifier.buildClassifier(train);

            double pred = evaluator.evaluateModelOnceAndRecordPrediction(copiedClassifier, insi);
            predArray[i] = pred;
            acc = acc + (pred == insi.classValue()?1:0);
//            System.out.println(pred + "\t" + insi.classValue());
            System.out.println((pred == insi.classValue()?1:0));
        }

        System.out.println(acc/data.numInstances());
        return predArray;
    }


    public static Instances readArffDataSet(String fileName) throws IOException{
        ArffLoader arffLoader = new ArffLoader();
        File file = new File(fileName);

        arffLoader.setFile(file);
        Instances data = arffLoader.getDataSet();

//        data.deleteAttributeAt(0);
        data.setClassIndex(data.numAttributes()-1);

        return data;
    }

    public static void main(String[] args) throws IOException, Exception{

        int numOfData = 8;
        for(int i = 0; i < numOfData; i++){
            System.out.println("Loading data set... \t" + dataFileNames[i]);
            String fileName = singleDataPath + dataFileNames[i] + "ARR10.arff";
            Instances data = readArffDataSet(fileName);
            IBk classifier = new IBk(100);
            double predicted[] = LeaveOneOutRecommendation(classifier, data);
            String stringResults = resultToString(predicted);

            String saveFileName = singleSavePath + "ARR10\\K=100\\"+saveFileNames[i]+".txt";
            System.out.println(stringResults);
            saveResults(saveFileName, stringResults);
        }
        
    }

    public static String resultToString(double[] predicted){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < predicted.length; i++){
            sb.append((predicted[i]+1)+"\n");
        }
        return sb.toString();
    }

    public static void saveResults(String fileName, String results){
        fileOperator fo = new fileOperator();
        fo.openWriteFile(fileName);
        fo.writeFile(results);
        fo.closeWriteFile();
    }
}
