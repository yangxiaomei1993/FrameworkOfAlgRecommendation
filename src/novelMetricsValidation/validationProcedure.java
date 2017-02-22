/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package novelMetricsValidation;

import dataCharacteristics.ArffFileCoding;
import dataCharacteristics.ComplexityBasedMetrics;
import dataCharacteristics.LandMarkMetrics;
import dataCharacteristics.ModelBasedMetrics;
import dataCharacteristics.newMetrics;
import dataCharacteristics.newMetricsXor;
import fileUtil.fileHandle;
import fileUtil.fileOperator;
import java.io.IOException;
import weka.core.FastVector;
import weka.core.Instances;

/**
 *
 * @author String
 */
public class validationProcedure {

    /**
     * metricMode:  1 - traditional
     *              2 - model based
     *              3 - land marker
     *              4 - complexity
     *              5 - our metrics (and)
     *              6 - our metrics (xor)
     * @param fileName
     * @return
     * @throws IOException
     */

    public static FastVector complexMetric;
    public static FastVector modelMetric;
    public static FastVector landMarkMetric;
    public static FastVector newMetric;
    public static FastVector newXorMetric;

    public static String metricSavePath = "E:\\My works\\Experiment results\\" +
            "Metrics Validation\\";
    public static String saveTextFilePath = "E:\\My works\\Experiment results\\" +
            "Metrics Validation\\Text file for Matlab\\";

    public static String arffDataFilePath = "E:\\My works\\Experiment results\\" +
            "Frameworks of Algorithm Recommendation\\UCI Data Sets\\Classification\\";

    public static void initialVector(){
        complexMetric = new FastVector();
        modelMetric = new FastVector();
        landMarkMetric = new FastVector();
        newMetric = new FastVector();
        newXorMetric = new FastVector();
    }

    public static void AchieveMetricsMatrix1(String fileName) throws IOException, Exception{
        Instances data = dataGenerator.readData(arffDataFilePath + fileName + ".arff");//Load the arff data set
        int numFolds = 20;
        Instances[] sampDataSets = dataGenerator.StratifiedSplitFolds(data, numFolds, 1);

        int fileID = 1;
        for(int i = 0; i < numFolds; i++){
            Instances sampData = sampDataSets[i];
            System.out.println(sampData.numInstances());
            saveDatasetIntextForm(sampData, saveTextFilePath + fileName + fileID + ".txt");
//            double[] complexityMetrics = ComplexityMetricExtaction(sampData);
//            complexMetric.addElement(complexityMetrics);

            double[] landMarkerMetrics = LandMarkerMetricExtraction(sampData);
            landMarkMetric.addElement(landMarkerMetrics);

            double[] modelBasedMetrics = ModelBasedMetricExtraction(sampData);
            modelMetric.addElement(modelBasedMetrics);

            double[] newAndMetrics = NewMetricExtraction(sampData);
            newMetric.addElement(newAndMetrics);

            double[] newXorMetrics = NewMetricXorExtraction(sampData);
            newXorMetric.addElement(newXorMetrics);

            fileID++;
        }

//        saveMetricMatrix(complexMetric, metricSavePath + fileName+ "_complexity.txt");
        saveMetricMatrix(landMarkMetric, metricSavePath + fileName+ "_landMarker.txt");
        saveMetricMatrix(modelMetric, metricSavePath + fileName+ "_modelBased.txt");
        saveMetricMatrix(newMetric, metricSavePath + fileName+ "_newAndMetric.txt");
        saveMetricMatrix(newXorMetric, metricSavePath + fileName+ "_newXorMetric.txt");
    }

    public static void AchieveMetricsMatrix(String fileName) throws IOException, Exception{
        Instances data = dataGenerator.readData(arffDataFilePath + fileName + ".arff");//Load the arff data set
        boolean isNoReplaced = false;
        int randomSeed = 1;
        double samPercent = 0;

        int fileID = 1;
        for(double sample = 60; sample <= 100.0; sample = sample + 2.0){
            samPercent = sample;
            Instances sampData = dataGenerator.Resample(data, isNoReplaced, randomSeed, samPercent);
            saveDatasetIntextForm(sampData, saveTextFilePath + fileName + fileID + ".txt");
//            double[] complexityMetrics = ComplexityMetricExtaction(sampData);
//            complexMetric.addElement(complexityMetrics);

            double[] landMarkerMetrics = LandMarkerMetricExtraction(sampData);
            landMarkMetric.addElement(landMarkerMetrics);

            double[] modelBasedMetrics = ModelBasedMetricExtraction(sampData);
            modelMetric.addElement(modelBasedMetrics);

            double[] newAndMetrics = NewMetricExtraction(sampData);
            newMetric.addElement(newAndMetrics);

            double[] newXorMetrics = NewMetricXorExtraction(sampData);
            newXorMetric.addElement(newXorMetrics);

            fileID++;
        }

//        saveMetricMatrix(complexMetric, metricSavePath + fileName+ "_complexity.txt");
        saveMetricMatrix(landMarkMetric, metricSavePath + fileName+ "_landMarker.txt");
        saveMetricMatrix(modelMetric, metricSavePath + fileName+ "_modelBased.txt");
        saveMetricMatrix(newMetric, metricSavePath + fileName+ "_newAndMetric.txt");
        saveMetricMatrix(newXorMetric, metricSavePath + fileName+ "_newXorMetric.txt");
    }

    public static void saveMetricMatrix(FastVector metricVector, String fileName){
        fileOperator fo = new fileOperator();

        fo.openWriteFile(fileName);
        for(int i = 0; i < metricVector.size(); i++){
            double[] metrics = (double[]) metricVector.elementAt(i);
            String line = data2Line(metrics);
            fo.writeFile(line);
        }
        fo.closeWriteFile();
    }

    public static String data2Line(double[] metrics){
        StringBuffer sb = new StringBuffer();
        sb.append(metrics[0]);
        for(int i = 1; i < metrics.length; i++){
            sb.append(","+metrics[i]);
        }
        return sb.toString();
    }

    public static void saveDatasetIntextForm(Instances data, String fileName){
        ArffFileCoding.dataCoding(data, fileName);
    }

    public static double[] ComplexityMetricExtaction(Instances data) throws Exception{
         ComplexityBasedMetrics cbm = new ComplexityBasedMetrics(data);
         cbm.computeComplexityMetrics();
         return cbm.achieveMetrics();
    }

    public static double[] ModelBasedMetricExtraction(Instances data) throws Exception{
        Instances delData = ModelBasedMetrics.setArffData(data);
        ModelBasedMetrics.ConstructDecitionTree(delData);
        
        ModelBasedMetrics.modelMetricCollection();
        return ModelBasedMetrics.CollectMetrics();
    }

    public static double[] LandMarkerMetricExtraction(Instances data) throws Exception{
        LandMarkMetrics.achieveMetrics(data);
        return LandMarkMetrics.achieveMetrics();
    }

    public static double[] NewMetricExtraction(Instances data) throws Exception{
        newMetrics nmExtractor = new newMetrics();
        nmExtractor.setArffDataset(data);
        if(nmExtractor.s_isOnlyClass){
            return null;
        }
        nmExtractor.computeMetrics();
        return nmExtractor.AchieveMetrics();
    }

    public static double[] NewMetricXorExtraction(Instances data) throws Exception{
        newMetricsXor nmExtractor = new newMetricsXor();
        nmExtractor.setArffDataset(data);
        if(nmExtractor.s_isOnlyClass){
            return null;
        }
        nmExtractor.computeMetrics();
        return nmExtractor.AchieveMetrics();
    }

    public static void clearVector(){
        complexMetric.removeAllElements();
        complexMetric.trimToSize();

        modelMetric.removeAllElements();
        modelMetric.trimToSize();
        
        landMarkMetric.removeAllElements();
        landMarkMetric.trimToSize();
        
        newMetric.removeAllElements();
        newMetric.trimToSize();
        
        newXorMetric.removeAllElements();
        newXorMetric.trimToSize();
    }

    public static void main(String[] args) throws IOException, Exception{
        fileHandle fh = new fileHandle();
        initialVector();
        AchieveMetricsMatrix1("dermatology");
        clearVector();
    }
}
