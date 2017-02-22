/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package learnAlgRecommendation;

import fileUtil.fileHandle;
import fileUtil.fileOperator;
import java.io.File;

/**
 *
 * @author String
 */
public class MetaTargetCollection {

//    public static final String IndResultPath = "E:\\My works\\Experiment results\\" +
//            "Frameworks of Algorithm Recommendation\\" +
//            "Classification Accuracy and Runtime\\";

//    public static final String IndResultPath = "E:\\My works\\Experiment results\\" +
//            "Frameworks of Algorithm Recommendation\\" +
//            "Classification Accuracy and Runtime\\";
//
////    public static final String fileNamePath = "E:\\My works\\Experiment results\\" +
////            "Frameworks of Algorithm Recommendation\\" +
////            "UCI Data Sets\\Classification\\";
//
//    public static final String fileNamePath = "E:\\My works\\Experiment results\\"
//            + "Frameworks of Algorithm Recommendation\\"
//            + "UCI Data Sets\\dataSetOids\\";
//
//    public static final String savePath = "E:\\My works\\Experiment results\\" +
//            "Frameworks of Algorithm Recommendation\\Meta-targets_Oid\\";
//
//    public static final String[] classifierNames = {"BayesNet", "Decision Tree",
//    "RandomTree", "CART", "JRip", "PART", "RBFNet", "SMO", "Boost+NB", "Boost+J48",
//    "Bag+NB", "Bag+J48"};

    public static final String IndResultPath = "E:\\My works\\Experiment results\\" +
            "Frameworks of Algorithm Recommendation\\" +
            "Classification Accuracy and Runtime (Combined)\\";

    public static final String fileNamePath = "E:\\My works\\Experiment results\\" +
            "Frameworks of Algorithm Recommendation\\UCI Data Sets\\All Data sets\\";

    public static final String[] classifierNames = {"BayesNet", "Decision Tree",
    "RandomTree", "Random Forest", "JRip", "PART", "RBFNet", "NNge", "SMO", "Boost+NB", "Boost+J48",
    "Bag+NB", "Bag+J48"};

    public static final String savePath = "E:\\My works\\Experiment results\\" +
            "Frameworks of Algorithm Recommendation\\" +
            "Classification Algorithm Recommendation\\Acc and Runtime\\";
    
    public static final int numOfAlgs = 13;

    public static String[] getDataFileNames(){
        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(fileNamePath, "arff", true);
        return fileNames;
    }

    public static double[] readDataLine(String fileName){
        File file = new File(fileName);
        if(!file.exists()){
            return null;
        }

        double[] data = new double[50];
        for(int i = 0; i < data.length; i++){
            data[i] = 0;
        }

        fileOperator fo = new fileOperator();
        fo.openReadFile(fileName);
        String line = fo.readByLine();
        String[] tokens = line.split(",");
        for(int i = 0; i < data.length; i++){
            data[i] = Double.parseDouble(tokens[i].toString().trim());
        }
        fo.closeReadFile();
        return data;
    }

    public static double[][] readDataArrayInfor(String dataName, String accOrTime){
        double[][] data = new double[numOfAlgs][50];
        for(int i = 0; i < numOfAlgs; i++){
            data[i] = new double[50];
            for(int j = 0; j < data[i].length; j++){
                data[i][j] = 0;
            }
        }

        for(int i = 0; i < numOfAlgs; i++){
            String absDataName = IndResultPath + classifierNames[i] + "\\" + dataName + "_" + accOrTime + ".txt";
            double[] dataLine = readDataLine(absDataName);
            if(dataLine!=null){
                for(int j = 0; j < 50; j++){
                    data[i][j] = dataLine[j];
                }
            }
        }
        return data;
    }

    public static void saveResults(String dataName, double[][] data){
        fileOperator fo = new fileOperator();
        fo.openWriteFile(dataName);
        for(int i = 0; i < 50; i++){
            String line = "" + data[0][i];
            for(int j = 1; j < numOfAlgs; j++){
                line = line + "," + data[j][i];
            }
            fo.writeFile(line);
        }

        fo.closeWriteFile();
    }

    public static void startRunning(){
        String[] dataFileNames = getDataFileNames();

        for(int i = 0; i < dataFileNames.length; i++){
            System.out.println((i+1)+"\t"+dataFileNames[i]);
            double[][] accs = readDataArrayInfor(dataFileNames[i], "Acc");
            double[][] times = readDataArrayInfor(dataFileNames[i], "Time");

            String accSavePath = savePath + "Accuracy\\" + dataFileNames[i] + ".txt";
            saveResults(accSavePath, accs);
            
            String timeSavePath = savePath + "Runtime\\" + dataFileNames[i] + ".txt";
            saveResults(timeSavePath, times);
        }
    }

    public static void main(String[] args){
        startRunning();
    }
}
