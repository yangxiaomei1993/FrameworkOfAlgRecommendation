/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metricMST;

import fileUtil.fileHandle;
import java.io.File;
import java.io.IOException;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 *
 * @author String
 */
public class metrics {
    protected String fileName;          //the file name of the data set with arff format
    protected ArffLoader arffLoader;    //the handle of the arff data set
    protected Instances data;           //the restore of the data set

    protected distanceMatrix disMatrix;
    protected MSTree mstree;
    protected rapidMSTree rapidmst;

    protected double s_classBound;

    protected double s_classBoundRapid;

    public metrics(){
    }
    
    public metrics(String name){
        fileName = name;
        s_classBound = 0;
        s_classBoundRapid = 0;
    }

    public void setArffData(Instances ins){
        s_classBound = 0;
        s_classBoundRapid = 0;

        data = ins;
    }

    public void readArffData() throws IOException{
        File file = new File(fileName);
        arffLoader = new ArffLoader();
        arffLoader.setFile(file);
        data = arffLoader.getDataSet();

//        System.out.print(data.numInstances()+"\t");
    }

    public void setDisMatrix(){
        disMatrix = new distanceMatrix(data);
        disMatrix.computeEntroAndDistance();
    }

    public void setMSTree(){
        mstree = new MSTree(disMatrix.s_HanminDistance, disMatrix.s_numOfInstances);
        mstree.intialParameters();
//        System.out.println("I am here!!");
        mstree.KruskalMST();

//        System.out.println(mstree.s_totalWeightInTree+"\t"+mstree.s_maxWeightInTree+"\t"+mstree.s_minWeightInTree);
//        mstree.showMST();
        computeClassBound();
    }

    public void setRapidMSTree(){
        rapidmst = new rapidMSTree(disMatrix.s_HanminDistance, disMatrix.s_numOfInstances);

        rapidmst.intialParameters();
        rapidmst.RapidKruskalMST();

//        System.out.println(rapidmst.s_totalWeightInTree+"\t"+rapidmst.s_maxWeightInTree+"\t"+rapidmst.s_minWeightInTree);
        computeClassBoundRapid();
    }

    public void computeClassBoundRapid() {
        int[] tempIndex = rapidmst.s_edgesInMSTree;
        int edgeIndex, row, col;
        int numOfInstances = disMatrix.s_numOfInstances;
        Instance insRow, insCol;
        for (int i = 0; i < tempIndex.length; i++) {
            edgeIndex = tempIndex[i];
            row = edgeIndex / numOfInstances;
            col = edgeIndex % numOfInstances;

//            System.out.println(edgeIndex);

            insRow = disMatrix.s_instances.instance(row);
            insCol = disMatrix.s_instances.instance(col);
            if (insRow.classValue() != insCol.classValue()) {
                s_classBoundRapid++;
            }
        }

        s_classBoundRapid = fourDecimal(s_classBoundRapid / (numOfInstances - 1.0));
    }


    public void computeClassBound(){
        int[] tempIndex = mstree.s_edgesInMSTree;
        int edgeIndex, row, col;
        int numOfInstances = disMatrix.s_numOfInstances;
        Instance insRow, insCol;
        for(int i = 0; i < tempIndex.length; i++){
            edgeIndex = tempIndex[i];
            row = edgeIndex/numOfInstances;
            col = edgeIndex%numOfInstances;

//            System.out.println(edgeIndex);

            insRow = disMatrix.s_instances.instance(row);
            insCol = disMatrix.s_instances.instance(col);
            if(insRow.classValue()!=insCol.classValue()){
                s_classBound++;
            }
        }

        s_classBound = fourDecimal(s_classBound/(numOfInstances-1.0));
    }

    public double fourDecimal(double d) {
        return Math.floor(d * 10000 + 0.5) / 10000;
    }

    public double getClassBound(){
        return s_classBound;
    }

    public double getClassBoundRapid(){
        return s_classBoundRapid;
    }

    public double[] getOneItemSets(){
        return disMatrix.getOneItemSets();
    }

    public double[] getTwoItemSets(){
        return disMatrix.getTwoItemSets();
    }

    public double[] getMutualInfors(){
        return disMatrix.getMultualInfors();
    }

    public double[] getTargetCounts(){
        return disMatrix.getClassLabelDistribution();
    }

    public double getRatioOfInforToNoise(){
        return disMatrix.getRatioOfInforToNoise();
    }

    public double getRatioOfInsToAttr(){
        return disMatrix.getRatioOfInsToAttr();
    }

    public double getClassEntro(){
        return disMatrix.getClassEntro();
    }

    public double getMeanEntro(){
        double meanEntro = 0;
        int count = 0;
        for(int i = 0; i < disMatrix.s_attrArray.length-1; i++){
            meanEntro = meanEntro + disMatrix.s_attrArray[i].s_entro;
            count++;
        }

        meanEntro = fourDecimal(meanEntro / count);

        return meanEntro;
    }

    public static void main(String []args) throws IOException{
//        String filePath = "E:\\My works\\papers\\A New Metric to Evaluate Different Feature Subset Selection Algorithms\\" +
//                "Experiment results\\synthetic data sets\\Experiment1\\";
//        String fileName = "100_100_0_1.arff";
////        String filePath = "C:\\Documents and Settings\\String\\桌面\\xyz.arff\\xyz.arff\\";
////        String fileName = "cmc.arff";
//
//
////        String fileName = "C:\\Documents and Settings\\String\\桌面\\soybean.arff";
////        String fileName = "F:\\resource\\数据挖掘算法的选择推荐\\real world data sets\\classification problems\\binaryProblems\\discrete\\kdd_SyskillWebert-Sheepdis.arff";
//        metrics met = new metrics(filePath+fileName);
//        met.readArffData();
//        met.setDisMatrix();
////        met.showDistanceMatrix();
//
////        System.out.println("I am here!!");
////        met.setMSTree();
//        met.setRapidMSTree();
////        met.showMSTree();
////        System.out.println(met.s_classBound + "\t" + met.s_classBoundRapid);
//         System.out.println(met.s_classBoundRapid+"\t"+fileName);
//        runMetrics(1000,100,1);

//        String filePath = "E:\\My works\\papers\\A New Metric to Evaluate Different Feature Subset Selection Algorithms\\"
//                + "Experiment results\\synthetic data sets\\RDG1\\1000-100-70\\";

//        String filePath = "E:\\My works\\papers\\A New Metric to Evaluate Different Feature Subset Selection Algorithms\\" +
//                "Experiment results\\real world data sets\\";

//        String filePath = "E:\\My works\\papers\\" +
//                "A New Metric to Evaluate Different Feature Subset Selection Algorithms\\" +
//                "Experiment results\\synthetic data sets\\Disjunction\\sample size\\";
//        String filePath = "C:\\Documents and Settings\\String\\桌面\\tempFiles\\";

//        String filePath = "E:\\My works\\papers\\A New Metric to Evaluate Different Feature Subset Selection Algorithms\\" +
//                "Experiment results\\synthetic data sets\\Parity\\irrelevant Features\\";
//        String filePath = "E:\\My works\\papers\\A New Metric to Evaluate Different Feature Subset Selection Algorithms\\" +
//                "Experiment results\\synthetic data sets\\Disjunction\\irrelevant Features\\";
//        String filePath = "E:\\My works\\papers\\A New Metric to Evaluate Different Feature Subset Selection Algorithms\\" +
//                "Experiment results\\synthetic data sets\\GMonks\\irrelevant Features\\";

//        String filePath = "E:\\My works\\papers\\A New Metric to Evaluate Different Feature Subset Selection Algorithms\\" +
//                "Experiment results\\synthetic data sets\\GMonks\\redundant features\\";

        String filePath = "E:\\My works\\papers\\A New Metric to Evaluate Different Feature Subset Selection Algorithms\\"
                + "Experiment results\\synthetic data sets\\GMonks\\redundant features\\";
        runMetrics(filePath);
    }

    public static void runMetrics(int numOfInstances, int numOfFeatures, int randomSeed) throws IOException {
//        String filePath = "E:\\My works\\papers\\A New Metric to Evaluate Different Feature Subset Selection Algorithms\\"
//                + "Experiment results\\synthetic data sets\\Experiment1\\";
        
        String filePath = "C:\\Documents and Settings\\String\\桌面\\tempFiles\\";
        String fileName = "";

        for(int i = 0; i < numOfFeatures; i++){
            fileName = numOfInstances+"_"+numOfFeatures+"_"+i+"_"+randomSeed+"r.arff";
            metrics met = new metrics(filePath+fileName);
            met.readArffData();
            met.setDisMatrix();
            met.setRapidMSTree();
            System.out.println(met.s_classBoundRapid+"\t"+fileName);
        }
    }

    public static void runMetrics(String filePath) throws IOException {
//        String filePath = "E:\\My works\\papers\\A New Metric to Evaluate Different Feature Subset Selection Algorithms\\"
//                + "Experiment results\\synthetic data sets\\Experiment1\\";

        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(filePath, "arff");
        String fileName = "";

        double[] cbls = new double[fileNames.length];
        int[] numbers = new int[fileNames.length];
        
        for (int i = 0; i < fileNames.length; i++) {
            fileName = fileNames[i];

//            System.out.println(fileName);
            metrics met = new metrics(filePath + fileName);
            met.readArffData();
            met.setDisMatrix();
            long start = System.currentTimeMillis();
            met.setRapidMSTree();
            long runtime = System.currentTimeMillis() - start;

            int numOfFeatures = met.data.numAttributes()-1;
            int numOfInstances = met.data.numInstances();
            cbls[i] = met.s_classBoundRapid;
            numbers[i] = numOfFeatures;
//            System.out.print(met.s_classBoundRapid + "\t" + numOfFeatures+"\t");
        }

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < fileNames.length; i++){
            sb.append(fileNames[i]+"\t"+cbls[i] + "\t"+numbers[i] + "\n");
        }
        sb.append("\n");

//        for (int i = 0; i < fileNames.length; i++) {
//            sb.append(cbls[i] + "\t");
//        }
//        sb.append("\n");
//        for (int i = 0; i < fileNames.length; i++) {
//            sb.append(numbers[i] + "\t");
//        }
//        sb.append("\n");

        System.out.println(sb.toString().trim());
    }

    public void showDistanceMatrix(){
        double[][] temp = disMatrix.s_HanminDistance;

        for(int i = 0; i < temp.length; i++){
            for(int j = 0; j < temp[i].length; j++){
                System.out.print(temp[i][j]+" ");
            }
            System.out.println();
        }
    }

    public void showMSTree(){
        int[] temp = mstree.s_edgesInMSTree;
        for(int i = 0; i < temp.length; i++){
            
            int row = temp[i]/disMatrix.s_numOfInstances;
            int col = temp[i]%disMatrix.s_numOfInstances;
            System.out.println(temp[i]+"\t"+row+"\t"+col+"\t"+mstree.s_weightOfGraph[row][col]);
        }

        System.out.println("Class Bound: "+s_classBound);
    }

    public Instances getDataSet(){
        return data;
    }
}