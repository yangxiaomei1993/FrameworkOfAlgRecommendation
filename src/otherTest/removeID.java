/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package otherTest;

import fileUtil.fileHandle;
import java.io.File;
import java.io.IOException;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;


/**
 *
 * @author String
 */
public class removeID {

    public final static String multiDataPath = "E:\\My works\\Experiment results"
            + "\\Frameworks of Algorithm Recommendation\\"
            + "Arff meta datasets\\Multi Labels\\";


    public static void main(String[] args) throws IOException{
        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(multiDataPath, "arff");
        for(int i = 0; i < fileNames.length; i++){
            Instances data = readArffDataSet(multiDataPath + fileNames[i]);
            saveDataset(data, fileNames[i]);
        }
    }

    public static Instances readArffDataSet(String fileName) throws IOException{
        ArffLoader arffLoader = new ArffLoader();
        File file = new File(fileName);

        arffLoader.setFile(file);
        Instances data = arffLoader.getDataSet();

        
        data.setClassIndex(data.numAttributes()-1);
        data.deleteAttributeAt(0);
        
        return data;
    }

    public static void saveDataset(Instances dataset, String fileName) throws IOException{
        ArffSaver arffSaver = new ArffSaver();
        File file = new File(multiDataPath + "\\RemoveID\\"+fileName);

        arffSaver.setFile(file);
        arffSaver.setInstances(dataset);
        arffSaver.writeBatch();
    }
}
