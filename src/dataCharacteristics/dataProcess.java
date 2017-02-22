/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataCharacteristics;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.RemoveUseless;

/**
 *
 * @author String
 */
public class dataProcess {

    protected String s_fileName;        //arff file name
    protected Instances s_dataset;      //arff data instances
    public boolean s_isOnlyClass;

    /*
     * Default constructor
     */
    public dataProcess(){
    }

    public dataProcess(String fileName){
        s_fileName = fileName;
    }

    public void readDataSet() throws Exception{
        try {
            ArffLoader arffLoader = new ArffLoader();
            File file = new File(s_fileName);
            arffLoader.setFile(file);

             s_dataset = arffLoader.getDataSet();
             int classIndex = s_dataset.numAttributes()-1;
             s_dataset.setClassIndex(classIndex);
             s_dataset.deleteWithMissingClass();
             
            if(isContainNumericAttribute(s_dataset)){
                Instances disData = dataDiscretization(s_dataset);
                Instances redData = removeUseless(disData);
                s_dataset = new Instances(redData, 0, redData.numInstances());
            }

            if(s_dataset.numAttributes() == 1){
                s_isOnlyClass = true;
            }else{
                s_isOnlyClass = false;
            }
        } catch (IOException ex) {
            Logger.getLogger(dataProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setArffDataset(Instances data) throws Exception{
        if (isContainNumericAttribute(data)) {
            Instances disData = dataDiscretization(data);
            Instances redData = removeUseless(disData);
            s_dataset = new Instances(redData, 0, redData.numInstances());
        }else{
            s_dataset = new Instances(data, 0, data.numInstances());
        }
        if (s_dataset.numAttributes() == 1) {
            s_isOnlyClass = true;
        } else {
            s_isOnlyClass = false;
        }
    }

    public boolean isContainNumericAttribute(Instances data){
        for(int i = 0; i < data.numAttributes() - 1; i++){
            if(data.attribute(i).isNumeric()){
                return true;
            }
        }
        return false;
    }

    public Instances dataDiscretization(Instances data) throws Exception{
        Discretize filter = new Discretize();
        filter.setInputFormat(data);
        filter.setMakeBinary(true);
        filter.setUseBetterEncoding(true);
        Instances disData = Filter.useFilter(data, filter);
        return disData;
    }

    public Instances removeUseless(Instances data) throws Exception{
        RemoveUseless filter = new RemoveUseless();
        filter.setInputFormat(data);
        Instances redData = Filter.useFilter(data, filter);
        return redData;
    }

    /*
     * Compute the number of the distinct feature values of feaIndex feature
     */
    public double[] countFeaAlone(int feaIndex){
        double[] counts = new double[s_dataset.attribute(feaIndex).numValues()];
        for(int i = 0; i < s_dataset.numInstances(); i++){
            Instance insi = s_dataset.instance(i);
            if(!insi.isMissing(feaIndex)){
                int ii = (int) insi.value(feaIndex);
                counts[ii]++;
            }
        }

        int nonZerosCount = 0;
        for(int i = 0; i < counts.length; i++){
            if(counts[i] > 0){
                nonZerosCount++;
            }
        }

        if(nonZerosCount != counts.length){
            double[] reduceCounts = new double[nonZerosCount];
            int index = 0;
            for(int i = 0; i < counts.length; i++){
                if(counts[i]>0){
                    reduceCounts[index++] = counts[i];
                }
            }
            return reduceCounts;
        }
        return counts;
    }
/**
 * The other index is set as the default index
 * classIndex
 * The feature value pair consists of the classification value and the value of
 * the given feature with feaIndex
 * @param feaIndex
 * @return
 */
    public double[][] countFeaPairs(int feaIndex){
        int rows = s_dataset.attribute(feaIndex).numValues();
        int cols = s_dataset.numClasses();
        double[][] counts = new double[rows][cols];

        for(int i = 0; i < s_dataset.numInstances(); i++){
            Instance insi = s_dataset.instance(i);
            if(!insi.isMissing(feaIndex)&&!insi.classIsMissing()){
                int rowIndex = (int) insi.value(feaIndex);
                int colIndex = (int) insi.classValue();
                counts[rowIndex][colIndex]++;
            }
        }
        return counts;
    }
/**
 * Collect the xor feature pairs frequency
 * @param feaIndex
 * @return
 */
    public double[][] countXorFeaPairs(int feaIndex){
        int rows = s_dataset.attribute(feaIndex).numValues();
        int cols = s_dataset.numClasses();

        boolean[] rowFlags = new boolean[rows];
        for(int i = 0; i < rowFlags.length; i++){
            rowFlags[i] = false;
        }
        boolean[] colFlags = new boolean[cols];
        for(int i = 0; i < colFlags.length; i++){
            colFlags[i] = false;
        }

        double[][] counts = new double[rows][cols];
        for(int i = 0; i < s_dataset.numInstances(); i++){
            Instance insi = s_dataset.instance(i);
            if(!insi.isMissing(feaIndex)&&!insi.classIsMissing()){
                int rowIndex = (int) insi.value(feaIndex);
                int colIndex = (int) insi.classValue();
                rowFlags[rowIndex] = true;
                colFlags[colIndex] = true;

                for(int j = 0; j < rows; j++){
                    if(j!=rowIndex){
                        counts[j][colIndex]++;
                    }
                }

                for(int j = 0; j < cols; j++){
                    if(j!=colIndex){
                        counts[rowIndex][j]++;
                    }
                }
            }
        }

        int rowDistinctNum = 0;
        int colDistinctNum = 0;
        for(int i = 0; i < rowFlags.length; i++){
            if(rowFlags[i]){
                rowDistinctNum++;
            }
        }

        for(int i = 0; i < colFlags.length; i++){
            if(colFlags[i]){
                colDistinctNum++;
            }
        }

        if((rowDistinctNum == rows)&&(colDistinctNum == cols)){
            return counts;
        }

        // if none of the feature values in rowIndex or colIndex appear,
        // the corresponding flag of rowIndex or colIndex is false.
        double[][] reducedCounts = new double[rowDistinctNum][colDistinctNum];
        int tempRowIndex = 0;
        for(int i = 0; i < counts.length; i++){
            if(rowFlags[i]){
                int tempColIndex = 0;
                for(int j = 0; j < counts[i].length; j++){
                    if(colFlags[j]){
                        reducedCounts[tempRowIndex][tempColIndex] = counts[i][j];
                        tempColIndex++;
                    }
                }
                tempRowIndex++;
            }
        }

        return reducedCounts;
    }

    public Instances getDataSet(){
        return this.s_dataset;
    }
}
