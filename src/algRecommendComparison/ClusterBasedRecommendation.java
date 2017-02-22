/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algRecommendComparison;

import fileUtil.fileOperator;
import java.util.Vector;
import mulan.classifier.lazy.MLkNN;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;

/**
 *
 * @author String
 */
public class ClusterBasedRecommendation {
    public final static String[] dataFileNames = {"complex_CluAcc","freqAnd_CluAcc",
    "freqXor_CluAcc", "landMark_CluAcc", "mixAnd_CluAcc", "mixXor_CluAcc",
    "modelBased_CluAcc", "trad_CluAcc"};
    public final static String[] saveFileNames = {"complex", "freqAnd", "freqXor",
    "landMark", "mixAnd", "mixXor", "modelBased", "tradMetrics"};
    public final static String multiDataPath = "E:\\My works\\Experiment results\\" +
            "Frameworks of Algorithm Recommendation\\Arff meta datasets\\Cluster Based Meta Data\\";

    public final static String multiSavePath = "E:\\My works\\Experiment results\\" +
            "Frameworks of Algorithm Recommendation\\Arff meta datasets\\Cluster Based Meta Data\\Results\\";


    public static void main(String[] args) throws InvalidDataFormatException{
        startRun();
    }

    public static void saveResults(String filePath, String fileName, String results){
        fileOperator fo = new fileOperator();
        fo.openWriteFile(filePath + fileName);
        fo.writeFile(results);
        fo.closeWriteFile();
    }

    public static String showResults(Evaluator eval, int numOfFolds){
        StringBuffer sb = new StringBuffer();
//        Vector[] predictMetrics = eval.obtainPredictMetrics();
//        Vector[] predictResults = eval.obtainPredictResults();
//        Vector[] predictRecMeth = eval.obtainReMethod();
//        Vector[] predictHit = eval.obtainHitInfor();
//        Vector[] predictLabels = eval.obtainRecLabels();
//
//        double[] insNumInFold = eval.obtainFoldIns();
//
//        for(int i = 0; i < numOfFolds; i++){
//            Vector metric = predictMetrics[i];
//            Vector result = predictResults[i];
//            Vector recMeth = predictRecMeth[i];
//            Vector hitVec = predictHit[i];
//            Vector preLabels = predictLabels[i];
//
//            sb.append(i+",");
//            for(int j = 0; j < insNumInFold[i]; j++){
//                double[] array = (double[]) metric.elementAt(j);
//                double intersect = Double.parseDouble(result.elementAt(j).toString());
//                int method = Integer.parseInt(recMeth.elementAt(j).toString());
//                double hit = Double.parseDouble(hitVec.elementAt(j).toString());
//                boolean[] labels = (boolean[]) preLabels.firstElement();
//
//                for(int k = 0; k < array.length; k++){
//                    sb.append(array[k]+",");
//                }
//
//                sb.append(intersect+","+method+","+hit+","+(labels[0]?1:0));
//
//                for(int k = 1; k < labels.length; k++){
//                    sb.append(","+(labels[k]?1:0));
//                }
//                sb.append("\n");
//            }
//        }
//
//        System.out.println(sb.toString());
        return sb.toString();
    }

    public static void startRun() throws InvalidDataFormatException{

        int numOfDataSets = 8;
        int numFolds = 1090;
        for (int i = 1; i < numOfDataSets; i++) {
            String arffFilename = dataFileNames[i]+".arff";
            String xmlFilename = "classLabels.xml";
            String saveFileName = saveFileNames[i] + ".txt";
            System.out.println(dataFileNames[i] +"\nLoading the dataset...");
            MultiLabelInstances dataset = new MultiLabelInstances(multiDataPath + arffFilename, multiDataPath + xmlFilename);

//            RAkEL learner1 = new RAkEL(new BinaryRelevance(new J48()));
//            BinaryRelevance learner1 = new BinaryRelevance(new J48());
            MLkNN learner2 = new MLkNN();
            Evaluator eval = new Evaluator();//evaluator中提供函数设置随机因子
            MultipleEvaluation results;
//            results = eval.crossValidate(learner1, dataset, numFolds);
//            System.out.println(results);
            results = eval.crossValidate(learner2, dataset, numFolds);
            String runResults = showResults(eval, numFolds);

            saveResults(multiSavePath, saveFileName, runResults);
            System.out.println(results);
        }

    }
}
