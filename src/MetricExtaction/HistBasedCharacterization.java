/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MetricExtaction;

import fileUtil.fileHandle;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Remove;

/**
 * 计算1项集频率和2-项集频率
 * 其中2-项集：两种，属性间构成的2项集，属性和目标概念之间构成的2项集
 * @author wgt
 */
public class HistBasedCharacterization {

    protected Instances s_train;
    protected int s_classIndex;
    protected int s_numOfAttribute;
    protected int s_numOfInstances;
    protected int s_numOfDistinctValues;
    protected int s_numOfDistinctClass;
    protected int s_numOfClass;
    protected int[] s_numOfAttrValues;
    protected boolean[][] s_valueFlags;
    protected Vector s_oneItemSet;
    protected Vector s_twoItemSetFF;
    protected Vector s_twoItemSetFC;
    protected boolean s_DiscretedFlag = false;
//==============================================================================
    protected int s_NBins = 10;
    protected double[] s_Intervals;
    protected boolean s_IntervalFlag = false;
//==============================================================================
    protected double[] s_oneItemDist;
    protected double[] s_twoItemFFDist;
    protected double[] s_twoItemFCDist;

    protected boolean s_oneItemIsNormal;
    protected boolean s_twoItemFFIsNormal;
    protected boolean s_twoItemFCIsNormal;
    
    protected double s_runtime;

//    protected double s_oneItemSkewness;
//    protected double s_twoItemFFSkewness;
//    protected double s_twoItemFCSkewness;
//
//    protected double s_ontItemKurtosis;
//    protected double s_twoItemFFKurtosiss;
//    protected double s_twoItemFCKurtosis;
/*******************************************************************************
 * =============================================================================
 ******************************************************************************/
    public HistBasedCharacterization() {
    }

    public void setDataSet(Instances train) throws Exception {
        train.deleteWithMissingClass();
        if (train.classIndex() < 0) {
            System.err.println("The input data set must be in the field of classification");
            System.exit(0);
        }

        s_train = new Instances(train, 0, train.numInstances());
        s_classIndex = s_train.classIndex();
        s_numOfAttribute = s_train.numAttributes();
        s_numOfInstances = s_train.numInstances();

        for (int i = 0; i < s_numOfAttribute; i++) {
            if (i != s_classIndex) {
                Attribute attri = s_train.attribute(i);
                if (!attri.isNominal()) {
                    s_DiscretedFlag = true;
                    break;
                }
            }
        }

        if (s_DiscretedFlag) {
            PreProcessing();
            s_numOfAttribute = s_train.numAttributes();
            s_classIndex = s_train.classIndex();
            if (s_numOfAttribute == 1) {
                System.err.println("There are only target attribute!");
                System.exit(1);
//                return;
            }
        }

        s_numOfDistinctValues = 0;
        s_numOfAttrValues = new int[s_numOfAttribute];
        s_valueFlags = new boolean[s_numOfAttribute][];

        s_oneItemSet = new Vector();

        for (int i = 0; i < s_numOfAttribute; i++) {
            AttributeStats attrStati = s_train.attributeStats(i);
            Attribute attri = s_train.attribute(i);
            int valueNum = attri.numValues();
            s_numOfAttrValues[i] = valueNum;
            s_numOfDistinctValues += attrStati.distinctCount;
            s_valueFlags[i] = new boolean[valueNum];
            int[] counts = attrStati.nominalCounts;
            for (int j = 0; j < counts.length; j++) {
                if (counts[j] > 0) {
                    s_oneItemSet.add(counts[j]);
                    s_valueFlags[i][j] = true;
                } else {
                    s_valueFlags[i][j] = false;
                }
            }
        }

        AttributeStats classStati = s_train.attributeStats(s_classIndex);
        s_numOfClass = s_train.numClasses();
        s_numOfDistinctClass = classStati.distinctCount;

//        System.out.println(s_numOfAttribute + "\t" + s_numOfInstances + "\t"
//                + s_numOfClass + "\t" + s_numOfDistinctClass + "\t"
//                + s_DiscretedFlag + "\t" + s_numOfDistinctValues);

        CollectTwoItemSet();
//        System.out.println(this.s_oneItemSet.size() + "\t" + this.s_twoItemSetFC.size() + "\t" + this.s_twoItemSetFF.size());
//        showFrequency();
    }

    private void PreProcessing() throws Exception {
        Discretize s_discretFilter = new Discretize();

        s_discretFilter.setInvertSelection(false);
        s_discretFilter.setMakeBinary(false);
        s_discretFilter.setUseBetterEncoding(false);
        s_discretFilter.setUseKononenko(false);

        s_discretFilter.setInputFormat(s_train);

        s_train = Filter.useFilter(s_train, s_discretFilter);

        Vector deleteCols = new Vector();//记录删除的属性的编号
        for (int i = 0; i < s_train.numAttributes(); i++) {
            if (s_train.numDistinctValues(i) <= 1) {
                deleteCols.addElement(new Integer(i));
            }
        }

        if (deleteCols.size() > 0) {
            Remove s_attributeFilter = new Remove();
            int[] todelete = new int[deleteCols.size()];
            for (int i = 0; i < deleteCols.size(); i++) {
                todelete[i] = ((Integer) (deleteCols.elementAt(i))).intValue();
            }
            s_attributeFilter.setAttributeIndicesArray(todelete);
            s_attributeFilter.setInvertSelection(false);
            s_attributeFilter.setInputFormat(s_train);
            s_train = Filter.useFilter(s_train, s_attributeFilter);
        }
    }

    private void SingleAttributeCollect() {
        int attrIndex = s_classIndex == 0 ? 1 : 0;
        Attribute attr = s_train.attribute(attrIndex);
        int numOfAttrValues = attr.numValues();
        double[][] counts = new double[numOfAttrValues][];

        for (int i = 0; i < counts.length; i++) {
            counts[i] = new double[this.s_numOfClass];
            for (int j = 0; j < counts[i].length; j++) {
                counts[i][j] = 0;
            }
        }

        for (int i = 0; i < s_numOfInstances; i++) {
            Instance insi = s_train.instance(i);
            if (!insi.isMissing(attrIndex)) {
                int fIndex = (int) insi.value(attrIndex);
                int cIndex = (int) insi.classValue();
                counts[fIndex][cIndex] += insi.weight();
            }
        }

        for (int i = 0; i < counts.length; i++) {
            for (int j = 0; j < counts[i].length; j++) {
                s_twoItemSetFC.add(counts[i][j]);
            }
        }
    }

    /**
     * Compute the frequency of the two itemsets
     */
    private void CollectTwoItemSet() {
        s_twoItemSetFF = new Vector();
        s_twoItemSetFC = new Vector();

        if (s_numOfAttribute <= 2) {
            System.err.println("There is only one feature!");
            SingleAttributeCollect();
            return;
        }

        int FFNum = (s_numOfAttribute - 1) * (s_numOfAttribute - 2) / 2;
        double[][][] FFItemCounts = new double[FFNum][][];
        double[][][] FCItemCounts = new double[s_numOfAttribute - 1][][];

        //Initialization
        int tempIndex = 0;
        for (int i = 0; i < s_numOfAttribute - 1; i++) {
            for (int j = i + 1; j < s_numOfAttribute - 1; j++) {                
                FFItemCounts[tempIndex] = new double[s_numOfAttrValues[i]][];
                for (int k = 0; k < FFItemCounts[tempIndex].length; k++) {
                    FFItemCounts[tempIndex][k] = new double[s_numOfAttrValues[j]];
                    for (int p = 0; p < FFItemCounts[tempIndex][k].length; p++) {
                        FFItemCounts[tempIndex][k][p] = 0;
                    }
                }
                tempIndex++;
            }
            FCItemCounts[i] = new double[s_numOfAttrValues[i]][];
            for (int j = 0; j < FCItemCounts[i].length; j++) {
                FCItemCounts[i][j] = new double[s_numOfClass];
                for (int k = 0; k < FCItemCounts[i][j].length; k++) {
                    FCItemCounts[i][j][k] = 0;
                }
            }
        }

        for (int i = 0; i < s_numOfInstances; i++) {
            Instance insi = s_train.instance(i);
            double classValue = insi.classValue();
            int classVIndex = (int) classValue;

            for (int j = 0; j < s_numOfAttribute - 1; j++) {
                if (!insi.isMissing(j)) {
                    int vjIndex = (int) insi.value(j);
                    FCItemCounts[j][vjIndex][classVIndex] += insi.weight();
                    for (int k = j + 1; k < s_numOfAttribute - 1; k++) {
                        if (!insi.isMissing(k)) {
                            int vkIndex = (int) insi.value(k);
                            int vIndex = (2*(s_numOfAttribute - 1) - (j+1))*j/2 + k-j-1;
//                            int vIndex = j * (s_numOfAttribute - 2) + (k - j - 1);
                            FFItemCounts[vIndex][vjIndex][vkIndex] += insi.weight();
                        }
                    }
                }
            }
        }

        for (int index = 0; index < FFItemCounts.length; index++) {
            double[][] counts = FFItemCounts[index];
            int[] RowCol = this.ParseIndex(index, s_numOfAttribute - 2);
//            System.out.println(RowCol[0] + "\t" + RowCol[1]);
            for (int i = 0; i < counts.length; i++) {
                for (int j = 0; j < counts[i].length; j++) {
                    if (counts[i][j] > 0) {
                        s_twoItemSetFF.add(counts[i][j]);
                    }
//                    else{
//                        if(this.s_valueFlags[RowCol[0]][i] && this.s_valueFlags[RowCol[1]][j]){
//                            s_twoItemSetFF.add(0);
//                        }
//                    }
                }
            }
        }

        for (int index = 0; index < FCItemCounts.length; index++) {
            double[][] counts = FCItemCounts[index];
            for (int i = 0; i < counts.length; i++) {
                for (int j = 0; j < counts[i].length; j++) {
                    if (counts[i][j] > 0) {
                        s_twoItemSetFC.add(counts[i][j]);
                    }
//                    else {
//                        if (this.s_valueFlags[index][i] && this.s_valueFlags[s_classIndex][j]) {
//                            s_twoItemSetFF.add(0);
//                        }
//                    }
                }
            }
        }
    }

    private int AchieveIntervalIndex(double value, double[] intervals){
        if(value <= intervals[0]){
            return 0;
        }

        if(value > intervals[intervals.length -1]){
            return intervals.length;
        }

        int index = 0;
        while(value > intervals[index]){
            index++;
        }

        return index;
    }

    protected double[] MetricExtraction(Vector freqVec){
        s_Intervals = new double[s_NBins - 1];
        for(int i = 0; i < s_Intervals.length; i++){
            s_Intervals[i] = 1.0/s_NBins * (i+1);
        }
        //======================================================================
//        System.out.println(java.util.Arrays.toString(s_Intervals));

        if(freqVec.size() == 0){
            double[] freqDist = new double[s_NBins + 2];
            for(int i = 0; i < freqDist.length; i++){
                freqDist[i] = Double.NaN;
            }

            return freqDist;
        }

        int totalNum = freqVec.size();
        double[] freqDist = new double[s_NBins + 2];

        double fourX = 0;
        double cubicX = 0;
        double squareX = 0;
        double sumX = 0;

        for(int i = 0; i < totalNum; i++){
            double valuei = Double.parseDouble(freqVec.elementAt(i).toString())/this.s_numOfInstances;
            double temp = valuei*valuei;
            sumX = sumX + valuei;
            squareX = squareX + temp;
            temp = temp*valuei;
            cubicX = cubicX + temp;
            temp = temp*valuei;
            fourX = fourX + temp;
            int intIndex = this.AchieveIntervalIndex(valuei, s_Intervals);
//            System.out.println(intIndex);
            freqDist[intIndex]++;
        }

        for(int i = 0; i < s_NBins; i++){
            freqDist[i] = fourDecimal(freqDist[i] / totalNum);
        }

        double S2 = (squareX - sumX*sumX/totalNum)/(totalNum - 1);
        double skewness = (totalNum*cubicX - 3*sumX*squareX + 2*sumX*sumX*sumX/totalNum)/((totalNum - 1.0)*(totalNum - 2.0));
        double g1 = skewness/(S2*Math.sqrt(S2));

        double kurtic = (fourX*(totalNum*totalNum*totalNum + totalNum*totalNum) - 4*(totalNum*totalNum + totalNum)*cubicX*sumX - 3*(totalNum*totalNum - totalNum)*squareX*squareX + 12*totalNum*squareX*sumX*sumX - 6*sumX*sumX*sumX*sumX)/(totalNum*(totalNum - 1.0)*(totalNum - 2.0)*(totalNum - 3.0));

        double g2 = kurtic/(S2*S2);
//        g2 = g2*(totalNum -2)*(totalNum - 3)/(totalNum*totalNum - 1) + 3*(totalNum - 1)/(totalNum + 1);
//        System.out.println(skewness + "\t" + kurtic);
        freqDist[s_NBins] = fourDecimal(g1);
        freqDist[s_NBins + 1] = fourDecimal(g2);
        return freqDist;
    }
    

    protected double[] MetricExtraction(int nbins, Vector freqVec){
        s_NBins = nbins;
        if(s_NBins <=1 ){
            System.err.println("Wrong range of the number of bins!");
            System.exit(1);
        }
        s_Intervals = new double[s_NBins - 1];
        for(int i = 0; i < s_Intervals.length; i++){
            s_Intervals[i] = 1.0/s_NBins * (i+1);
        }
        //======================================================================
        if (freqVec.size() == 0) {
            double[] freqDist = new double[s_NBins + 2];
            for (int i = 0; i < freqDist.length; i++) {
                freqDist[i] = Double.NaN;
            }

            return freqDist;
        }

        int totalNum = freqVec.size();
        double[] freqDist = new double[s_NBins + 2];

        double fourX = 0;
        double cubicX = 0;
        double squareX = 0;
        double sumX = 0;

        for(int i = 0; i < totalNum; i++){
            double valuei = Double.parseDouble(freqVec.elementAt(i).toString())/this.s_numOfInstances;
            double temp = valuei*valuei;
            sumX = sumX + valuei;
            squareX = squareX + temp;
            temp = temp*valuei;
            cubicX = cubicX + temp;
            temp = temp*valuei;
            fourX = fourX + temp;
            int intIndex = this.AchieveIntervalIndex(valuei, s_Intervals);
//            System.out.println(intIndex);
            freqDist[intIndex]++;
        }

        for(int i = 0; i < s_NBins; i++){
            freqDist[i] = fourDecimal(freqDist[i] / totalNum);
        }

        double S2 = (squareX - sumX*sumX/totalNum)/(totalNum - 1);
        double skewness = (totalNum*cubicX - 3*sumX*squareX + 2*sumX*sumX*sumX/totalNum)/((totalNum - 1.0)*(totalNum - 2.0));
        double g1 = skewness/(S2*Math.sqrt(S2));

        double kurtic = (fourX*(totalNum*totalNum*totalNum + totalNum*totalNum) - 4*(totalNum*totalNum + totalNum)*cubicX*sumX -
                3*(totalNum*totalNum + totalNum)*squareX*squareX + 12*totalNum*squareX*sumX*sumX - 6*sumX*sumX*sumX*sumX)/(totalNum*(totalNum - 1.0)*(totalNum - 2.0)*totalNum - 3.0);

        double g2 = kurtic/(S2*S2);


        freqDist[s_NBins] = fourDecimal(g1);
        freqDist[s_NBins + 1] = fourDecimal(g2);
        return freqDist;

    }

    protected double[] MetricExtraction(double[] bins, Vector freqVec){
        if(this.s_IntervalFlag){
            s_Intervals = new double[bins.length];
            System.arraycopy(bins, 0, s_Intervals, 0, bins.length);
            s_NBins = s_Intervals.length + 1;
        }else{
            s_NBins = bins.length;
            if (s_NBins <= 1) {
                System.err.println("Wrong range of the number of bins!");
                System.exit(1);
            }

            s_Intervals = new double[s_NBins - 1];
            for(int i = 0; i < s_Intervals.length; i++){
                s_Intervals[i] = (bins[i] + bins[i+1])/2;
            }
        }
        //======================================================================
        if (freqVec.size() == 0) {
            double[] freqDist = new double[s_NBins + 2];
            for (int i = 0; i < freqDist.length; i++) {
                freqDist[i] = Double.NaN;
            }

            return freqDist;
        }
        
        int totalNum = freqVec.size();
        double[] freqDist = new double[s_NBins + 2];

        double fourX = 0;
        double cubicX = 0;
        double squareX = 0;
        double sumX = 0;

        for(int i = 0; i < totalNum; i++){
            double valuei = Double.parseDouble(freqVec.elementAt(i).toString())/this.s_numOfInstances;
            double temp = valuei*valuei;
            sumX = sumX + valuei;
            squareX = squareX + temp;
            temp = temp*valuei;
            cubicX = cubicX + temp;
            temp = temp*valuei;
            fourX = fourX + temp;
            int intIndex = this.AchieveIntervalIndex(valuei, s_Intervals);
//            System.out.println(intIndex);
            freqDist[intIndex]++;
        }

        for(int i = 0; i < s_NBins; i++){
            freqDist[i] = fourDecimal(freqDist[i] / totalNum);
        }

        double S2 = (squareX - sumX*sumX/totalNum)/(totalNum - 1);
        double skewness = (totalNum*cubicX - 3*sumX*squareX + 2*sumX*sumX*sumX/totalNum)/((totalNum - 1.0)*(totalNum - 2.0));
        double g1 = skewness/(S2*Math.sqrt(S2));

        double kurtic = (fourX*(totalNum*totalNum*totalNum + totalNum*totalNum) - 4*(totalNum*totalNum + totalNum)*cubicX*sumX -
                3*(totalNum*totalNum + totalNum)*squareX*squareX + 12*totalNum*squareX*sumX*sumX - 6*sumX*sumX*sumX*sumX)/(totalNum*(totalNum - 1.0)*(totalNum - 2.0)*totalNum - 3.0);

        double g2 = kurtic/(S2*S2);

        
        freqDist[s_NBins] = fourDecimal(g1);
        freqDist[s_NBins + 1] = fourDecimal(g2);
        return freqDist;
    }

    private double fourDecimal(double d){
        return Math.floor(d*10000 + 0.5)/10000;
    }

    /**
     * 
     * @param index
     * @param numOfAttr 属性数量-1
     * @return
     */
    private int[] ParseIndex(int index, int numOfAttr){
        index = index + 1;
        int[] RowCol = new int[2];
        RowCol[0] = 0;
        RowCol[1] = 0;

        int sum = numOfAttr;
        int row = 1;
        int preSum = 0;
        while(sum < index){
            preSum = preSum + numOfAttr - row + 1;
            sum = sum + numOfAttr - row;
            row = row + 1;            
        }
        int col = index - preSum;
        col = col + row;
        RowCol[0] = row - 1;
        RowCol[1] = col - 1;
//        System.out.println(index+ "\t" + row + "\t" + col);
        return RowCol;
    }

    public void CaculateMetrics(){
        s_oneItemDist = MetricExtraction(s_oneItemSet);
        s_twoItemFFDist = MetricExtraction(s_twoItemSetFF);
        s_twoItemFCDist = MetricExtraction(s_twoItemSetFC);
    }

    public void CaculateMetrics(int nbins){
        s_oneItemDist = MetricExtraction(nbins, s_oneItemSet);
        s_twoItemFFDist = MetricExtraction(nbins, s_twoItemSetFF);
        s_twoItemFCDist = MetricExtraction(nbins, s_twoItemSetFC);
    }

    public void CaculateMetrics(double[] nbins){
        s_oneItemDist = MetricExtraction(nbins, s_oneItemSet);
        s_twoItemFFDist = MetricExtraction(nbins, s_twoItemSetFF);
        s_twoItemFCDist = MetricExtraction(nbins, s_twoItemSetFC);
    }

    public void setInterFlag(boolean flag){
        s_IntervalFlag = flag;
    }

    public String printMetrics(){
        StringBuilder sb = new StringBuilder();
        if (this.s_numOfDistinctClass > 1) {
            sb.append(this.printArray(this.s_oneItemDist));
            sb.append(",");
            sb.append(this.printArray(this.s_twoItemFCDist));
            sb.append(",");
            sb.append(this.printArray(this.s_twoItemFFDist));
        } else {
            sb.append(this.printArray(this.s_oneItemDist));
            sb.append(",");
            sb.append(this.printArray(this.s_twoItemFFDist));
        }
        return sb.toString();
    }

    public String printArray(double[] array){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(i = 0; i < array.length - 3; i++){
            sb.append(array[i]+",");
        }
        sb.append(array[i]);

        return sb.toString();
    }

    public void showFrequency() {
        System.out.println("===========================The one item set: " + s_oneItemSet.size());
        for (int i = 0; i < s_oneItemSet.size(); i++) {
            double count = Double.parseDouble(s_oneItemSet.elementAt(i).toString()) / this.s_numOfInstances;
            System.out.println(count);
        }
//        System.out.println("===========================");
//        double[] oneDist = this.MetricExtraction(s_oneItemSet);
//        System.out.println(java.util.Arrays.toString(oneDist));

        System.out.println("===========================The two item set: " + s_twoItemSetFC.size());
        for (int i = 0; i < s_twoItemSetFC.size(); i++) {
            double count = Double.parseDouble(s_twoItemSetFC.elementAt(i).toString()) / this.s_numOfInstances;
            System.out.println(count);
        }
//        System.out.println("===========================");
//        double[] twoFCDist = this.MetricExtraction(s_twoItemSetFC);
//        System.out.println(java.util.Arrays.toString(twoFCDist));

//        System.out.println("===========================The two item set: " + s_twoItemSetFF.size());
//        for (int i = 0; i < s_twoItemSetFF.size(); i++) {
//            double count = Double.parseDouble(s_twoItemSetFF.elementAt(i).toString()) / this.s_numOfInstances;
//            System.out.println(count);
//        }
        System.out.println("===========================");
        double[] twoFFDist = this.MetricExtraction(s_twoItemSetFF);
        System.out.println(java.util.Arrays.toString(twoFFDist));

    }

    public static void showMetricsOfFolder(String folderName) throws Exception{
        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(folderName, "arff");
        for (int i = 0; i < fileNames.length; i++) {

            System.out.print( i + "," + fileNames[i]+",");
            String filePath = folderName + fileNames[i];
            Instances data = readArffData(filePath);

            long startTime = System.nanoTime();
            HistBasedCharacterization HBC = new HistBasedCharacterization();
            HBC.setDataSet(data);
            HBC.CaculateMetrics();
            String metricLine = HBC.printMetrics();
            long endTime = System.nanoTime();
            double runtime = (endTime - startTime)/1000000.0;
//            System.out.println(metricLine + "," + runtime);
            System.out.println(runtime);
        }
    }
    
    

    public static void main(String[] args) throws IOException, Exception {
        
        String fileFolderName = "E:\\My works\\Experiment results\\"
                + "Classification Algorithm Recommendation by Clustering\\Data set\\Orig data sets\\";
        showMetricsOfFolder(fileFolderName);
  
//        for(int i = 1; i < 37; i++){
//            System.out.print("X_" + i + "\t");
//        }
//        String fileFolderName = "F:\\experiment results\\PCABasedClassifier\\Orig data sets\\";
//        showMetricsOfFolder(fileFolderName);
//        String fileName = "C:\\Users\\wgt\\Desktop\\test.arff";
//        String fileName = "F:\\experiment results\\PCABasedClassifier\\Orig data sets\\liver-disorders.arff";
//        Instances data = readArffData(fileName);
//
//        HistBasedCharacterization HBC = new HistBasedCharacterization();
//        HBC.setDataSet(data);
//        HBC.CaculateMetrics();
//        HBC.showFrequency();
//        String metricLine = HBC.printMetrics();
//
//        System.out.println(metricLine);
//        double[] temp = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1};
//        System.out.println(HBC.AchieveIntervalIndex(0.11,temp));
    }

    static Instances readArffData(String fileName) throws IOException {
        File file = new File(fileName);
        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setFile(file);

        Instances data = arffLoader.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        return data;
    }

    public int getNumOfClasses(){
        return this.s_numOfDistinctClass;
    }
}
