/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataPreprocess;

import fileUtil.fileHandle;
import java.io.File;
import java.io.IOException;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

/**
 *
 * @author String
 */
public class DatasetOid {

    public static String datasetOidPath = "C:\\Users\\Administrator\\Desktop\\sample-datasetoids\\";

    public static String origFilePath = "C:\\Users\\Administrator\\Desktop\\sample\\";

    public static String myFileName;

    public static FastVector nominalAttrIndexes(Instances data){
        FastVector nomIndexes = new FastVector();
        int numOfAttributes = data.numAttributes() - 1;
        for(int i = 0; i < numOfAttributes; i++){
            if(data.attribute(i).isNominal()){
                nomIndexes.addElement(i);
            }
        }
        return nomIndexes;
    }

    public static Instances genDatasetOid(Instances data, int nomAttrIndex){
        Instances dataoid = new Instances(data, 0, data.numInstances());
        dataoid.deleteWithMissing(nomAttrIndex);
        Attribute nomAttr = dataoid.attribute(nomAttrIndex);
        dataoid.insertAttributeAt(nomAttr, dataoid.classIndex()+1);
        dataoid.setClassIndex(dataoid.numAttributes()-1);
        for(int i=0 ; i< dataoid.numInstances(); i++){
            double classValue=dataoid.instance(i).value(nomAttrIndex);
            dataoid.instance(i).setClassValue(classValue);
        }
        dataoid.deleteAttributeAt(nomAttrIndex);
        return dataoid;
    }

    public static boolean isProperProblem(Instances data){
        int numOfAttributes = data.numAttributes();
        AttributeStats attrStat = data.attributeStats(numOfAttributes-1);
        int[] counts = attrStat.nominalCounts;

        double sumCount = 0;
        double maxCount = Double.NEGATIVE_INFINITY;
        double minCount = Double.POSITIVE_INFINITY;
        int zeroCount = 0;


        for(int i = 0; i < counts.length; i++){
            sumCount = sumCount + counts[i];

            if(counts[i] == 0){
                zeroCount++;
            }else{
                if(counts[i] < minCount){
                    minCount = counts[i];
                }

                if(counts[i] > maxCount){
                    maxCount = counts[i];
                }
            }
        }

        if(zeroCount == (counts.length -1)){
            return false;
        }

        if(sumCount <= 10){
            return false;
        }

        if(maxCount/minCount >= 100){
            return false;
        }

        return true;
    }

    public static void genDatasetOids(Instances data) throws IOException{
        FastVector nomAttrIndexes = nominalAttrIndexes(data);
        if(nomAttrIndexes.size() == 0){
            return;
        }

        for(int i = 0; i < nomAttrIndexes.size(); i++){
            int nomIndex = Integer.parseInt(nomAttrIndexes.elementAt(i).toString());
            Instances datasetoid = genDatasetOid(data, nomIndex);
            boolean isProper = isProperProblem(datasetoid);
            System.out.println(myFileName + datasetoid.attribute(nomIndex).name());
            if (isProper) {
                saveDatasetOids(datasetoid, datasetOidPath + myFileName + nomIndex + ".arff");
            }
        }
        
    }

    public static void saveDatasetOids(Instances data, String fileName) throws IOException{
        ArffSaver arffSaver = new ArffSaver();
        File saveFile = new File(fileName);
        arffSaver.setFile(saveFile);
        arffSaver.setInstances(data);
        arffSaver.writeBatch();
    }

    public static Instances readDataSet(String fileName) throws IOException {
        ArffLoader arffLoader = new ArffLoader();
        myFileName = fileName;
        File file = new File(origFilePath + myFileName +".arff");
        arffLoader.setFile(file);

        Instances dataset = arffLoader.getDataSet();
        int classIndex = dataset.numAttributes() - 1;
        dataset.setClassIndex(classIndex);

        return dataset;
    }

    public static void main(String[] args) throws IOException{
        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(origFilePath, "arff", true);

        for(int i = 0; i < fileNames.length; i++){
            System.out.println(fileNames[i]);
            Instances data = readDataSet(fileNames[i]);
            genDatasetOids(data);
        }
    }
}
