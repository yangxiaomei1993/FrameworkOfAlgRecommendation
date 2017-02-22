/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataCharacteristics;

import fileUtil.fileHandle;
import fileUtil.fileOperator;
import java.io.File;
import java.io.IOException;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * 为了方便利用 matlab 进行处理
 *  @author String
 */
public class ArffFileCoding {

    public static Instances readDataSet(String fileName) throws IOException {
        ArffLoader arffLoader = new ArffLoader();
        File file = new File(fileName);
        arffLoader.setFile(file);

        Instances dataset = arffLoader.getDataSet();
        int classIndex = dataset.numAttributes() - 1;
        dataset.setClassIndex(classIndex);

        return dataset;
    }

    public static void dataCoding(Instances arffData, String saveFileName){
        int numOfFeatures = arffData.numAttributes();
        boolean[] isNominal = new boolean[numOfFeatures-1];
        for(int i = 0; i < numOfFeatures-1; i++){
            if(arffData.attribute(i).isNominal()){
                isNominal[i] = true;
            }else{
                isNominal[i] = false;
            }
        }

        fileOperator fo = new fileOperator();
        fo.openWriteFile(saveFileName);
        String titleLine = titleLine(isNominal);
        fo.writeFile(titleLine);

        for(int i = 0; i < arffData.numInstances(); i++){
            Instance insi = arffData.instance(i);
            if(!insi.classIsMissing()){
                String dataLine = codeInstanceLine(insi);
                fo.writeFile(dataLine);
            }     
        }
        fo.closeWriteFile();
    }

    public static String titleLine(boolean[] flags){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < flags.length; i++){
            if(flags[i]){
                sb.append("1,");
            }else{
                sb.append("0,");
            }
        }

        sb.append("1");

        System.out.println(sb.toString());
        return sb.toString();
    }

    public static String codeInstanceLine(Instance ins){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < ins.numAttributes() - 1; i++){
            if(ins.isMissing(i)){
                sb.append("nan,");
            }else{
                sb.append(ins.value(i)+",");
            }
        }
        sb.append(ins.classValue());

        return sb.toString();
    }

    public static void main(String[] args) throws IOException{
//        String dataFilePath = "E:\\My works\\Experiment results\\" +
//                "Frameworks of Algorithm Recommendation\\UCI Data Sets\\Classification\\";

        String dataFilePath = "D:\\My works\\Experiment results\\" +
                "Frameworks of Algorithm Recommendation\\UCI Data Sets\\All Data sets\\";

        String txtDataSavePath = "D:\\My works\\Experiment results\\" +
                "Frameworks of Algorithm Recommendation\\UCI Data Sets\\txtForm\\";

        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(dataFilePath, "arff", true);

        for(int i = 0; i < fileNames.length; i++){
            System.out.println(fileNames[i]);
            Instances data = readDataSet(dataFilePath + fileNames[i] + ".arff");
            dataCoding(data, txtDataSavePath + fileNames[i] + ".txt");
        }
    }
}
