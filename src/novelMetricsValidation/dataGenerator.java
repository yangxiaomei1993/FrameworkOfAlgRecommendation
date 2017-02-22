/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package novelMetricsValidation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.StratifiedRemoveFolds;

/**
 * Random sample
 * Random split the data set into several subsets
 * @author String
 */
public class dataGenerator {

    public static Instances Resample(Instances data, boolean noReplaced, int randomSeed, double percent) throws Exception{
        Resample filter = new Resample();
        filter.setRandomSeed(randomSeed);
        filter.setInputFormat(data);
        filter.setNoReplacement(noReplaced);
        filter.setSampleSizePercent(percent);
        Instances resample = Filter.useFilter(data, filter);
        return resample;
    }

    public static Instances[] StratifiedSplitFolds(Instances data, int folds, long randomSeed) throws Exception{
        Instances[] splitFolds = new Instances[folds];
        for(int fold = 1; fold <= folds; fold++){
            StratifiedRemoveFolds filter = new StratifiedRemoveFolds();
            filter.setNumFolds(folds);
            filter.setInputFormat(data);
            filter.setFold(fold);
            filter.setInvertSelection(true);
            filter.setSeed(randomSeed);
            splitFolds[fold - 1] = Filter.useFilter(data, filter);
        }        
        return splitFolds;
    }

    public static Instances readData(String fileName) throws IOException {
        File file = new File(fileName);
        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setFile(file);
        Instances data = arffLoader.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        data.deleteWithMissingClass();
        return data;
    }

    public static void main(String[] args) throws IOException, Exception{
		FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\sample\\test\\skin.arff",true);
		PrintStream ps = new PrintStream(out);
		System.setOut(ps);
        String filePath = "E:\\experiment\\dataset\\UCI\\big\\";

        String fileName = "skin.arff";        
        Instances data = readData(filePath + fileName);
        Instances sampleData = Resample(data, false, 1, 8);
        System.out.println(sampleData.toString());
        //System.out.println(sampleData.numInstances());
    }
}
