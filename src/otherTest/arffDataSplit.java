/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package otherTest;

import java.io.File;
import java.io.IOException;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

/**
 *
 * @author String
 */
public class arffDataSplit {

    public static String saveFilePath = "E:\\My works\\Experiment results\\" +
                "Algorithm Recommendation\\Novel data set characteristics\\" +
                "metrics\\new2\\";

    public static void main(String []args) throws IOException{
        String filePath = "E:\\My works\\Experiment results\\" +
                "Algorithm Recommendation\\Novel data set characteristics\\metrics\\";

        String fileName = "newMetric2.arff";

        ArffLoader arffLoader = new ArffLoader();
        File file = new File(filePath + fileName);

        arffLoader.setFile(file);
        Instances dataset = arffLoader.getDataSet();

        int startIndex = 20;
        int endIndex = 29;

        for(int index = startIndex; index <=endIndex; index++){
            removeRedundantFeatures(startIndex,
        index, endIndex, dataset);
        }
    }

    public static void removeRedundantFeatures(int startIndex,
        int reserveIndex, int endIndex, Instances dataset) throws IOException{

        Instances tempData = new Instances(dataset);
        for(int i = endIndex; i > reserveIndex; i--){
            tempData.deleteAttributeAt(i);
        }

        for(int i = reserveIndex-1; i >=startIndex; i--){
            tempData.deleteAttributeAt(i);
        }

        String fileName = tempData.attribute(startIndex).name();
        tempData.deleteAttributeAt(0);
        tempData.deleteAttributeAt(0);
        saveDataset(tempData, fileName);
    }

    public static void saveDataset(Instances dataset, String fileName) throws IOException{
        ArffSaver arffSaver = new ArffSaver();
        File file = new File(saveFilePath+fileName+".arff");

        arffSaver.setFile(file);
        arffSaver.setInstances(dataset);
        arffSaver.writeBatch();
    }

}
