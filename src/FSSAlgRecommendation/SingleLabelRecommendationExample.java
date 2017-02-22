/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FSSAlgRecommendation;

import fileUtil.fileOperator;
import DecisionTree.J48;
import fileUtil.fileHandle;
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
 *
 * @author String
 */
public class SingleLabelRecommendationExample {
    public final static String singleDataPath = "E:\\Phd paper experimental results\\Meta-data\\Singlelabel\\";
    public final static String singleSavePath = "E:\\Phd paper experimental results\\Meta-data\\SingleResults\\";

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

        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(singleDataPath, "arff");
        int numOfData = fileNames.length;
        for(int i = 0; i < numOfData; i++){
            System.out.println("Loading data set... \t" + fileNames[i]);
            String fileName = singleDataPath + fileNames[i];
            Instances data = readArffDataSet(fileName);
            IBk classifier = new IBk(30);
            double predicted[] = LeaveOneOutRecommendation(classifier, data);
            String stringResults = resultToString(predicted);

            String saveFileName = singleSavePath + fileNames[i] + ".txt";
            System.out.println(stringResults);
//            saveResults(saveFileName, stringResults);
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
