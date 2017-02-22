/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metricMST;

import java.util.Vector;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Compute and store the disances of any two instances
 * Meanwhile, the frequence of the 1-item sets and 2-item sets, entroy of the
 * target concept, the mutual information of each feature 
 * @author String
 */
public class distanceMatrix {
    protected Instances s_instances;    //data set
    protected int s_numOfInstances;     //number of the instances in the data set
    protected int s_numOfAttribute;     //number of the features in the data set

    protected singleAttribute[] s_attrArray;    //information of all single attributes
    //information of all pairs of attributes with vector form, just records the pair (a feature and the target concept)
    protected AttributePairs[] s_attrPairsArray;
    
//    protected AttributePairs[][] s_attrPairsMatrix; //information of all pairs of attributes with matrix form

    protected double[] s_conditionEntro;    //mutual entropy for all attributes with the target concept
    protected double[] s_multualInfors;
    protected double s_classEntro;          //entropy of the target concept
    protected double s_ratioOfInsAndAttr;   //ratio of instances and attribute
    protected double s_ratioOfIN;           //ratio of information and noise

    protected double[][] s_EuclideanDistance;   //euclidean distances of all pais of instances
    protected double[][] s_HanminDistance;      //hanmin distances of all pairs of intances

    /**
     * Default constructor function
     */
    public distanceMatrix(){
    }

    /**
     * Construction function
     * @param instances
     */
    public distanceMatrix(Instances instances){
        s_instances = instances;
        s_numOfInstances = s_instances.numInstances();
        s_numOfAttribute = s_instances.numAttributes();

        s_instances.setClassIndex(s_numOfAttribute-1);//set the class concept index

        s_attrArray = new singleAttribute[s_numOfAttribute];//initialize all the single attributes

        s_attrPairsArray = new AttributePairs[s_numOfAttribute-1];//initialize all pairs of features and target concept

        s_conditionEntro = new double[s_numOfAttribute - 1];    //intialize the multal informations
        s_multualInfors = new double[s_numOfAttribute - 1];
        for(int i = 0; i < s_conditionEntro.length; i++){
            s_conditionEntro[i] = 0;
            s_multualInfors[i] = 0;
        }
//        s_attrPairsMatrix = new AttributePairs[s_numOfAttribute-1][];

        //**********************************************************************
        s_EuclideanDistance = new double[s_numOfInstances][]; //initialize the Euclidean distance matrix
        s_HanminDistance = new double[s_numOfInstances][];    //initialize the Hanmin distance matrix

        for(int i = 0; i < s_numOfInstances; i++){
            s_EuclideanDistance[i] = new double[s_numOfInstances];
            s_HanminDistance[i] = new double[s_numOfInstances];
            for(int j = 0; j < s_numOfInstances; j++){
                s_EuclideanDistance[i][j] = 0;
                s_HanminDistance[i][j] = 0;
            }
        }
         //**********************************************************************
        
        s_classEntro = 0;   //initialize the entropy of the target concept
        s_ratioOfInsAndAttr = 0;    //initialize the ratio of instance and attribute
        s_ratioOfIN = 0;            //initialize the ratio of information and noise
    }

    /**
     * initialize the useful parameters
     * all the single attributes and pairs of features and target concept
     */
    public void initialParameters(){
        Attribute attr = null;      //the attribute
        String name = null;         //name of the attribute
        String[] values = null;     //values of the attribute
        for(int i = 0; i < s_attrArray.length; i++){
            attr = s_instances.attribute(i);
            name = attr.name();
            values = new String[attr.numValues()];
            for(int j = 0; j < values.length; j++){
                values[j] = attr.value(j);
            }
            s_attrArray[i] = new singleAttribute(name,values);
//            System.out.println(s_attrArray[i].s_attrName+" "+s_attrArray[i].s_numOfValues);
        }

        for(int i = 0; i < s_attrPairsArray.length; i++){
            s_attrPairsArray[i] = new AttributePairs(s_attrArray[i], s_attrArray[s_numOfAttribute-1]);
        }
//        for(int i = 0; i < s_attrPairsMatrix.length; i++){
//            s_attrPairsMatrix[i] = new AttributePairs[s_numOfAttribute];
//            for(int j = i+1; j < s_attrPairsMatrix[i].length; j++){
//                s_attrPairsMatrix[i][j] = new AttributePairs(s_attrArray[i], s_attrArray[j]);
//            }
//        }
    }

    /**
     * Read the instances in the given data set
     */
    public void readInstances() {
        for (int i = 0; i < s_numOfInstances; i++) {
            Instance insi = s_instances.instance(i);
            for (int j = 0; j < s_numOfAttribute - 1; j++) {
                if (!insi.isMissing(j) && !insi.classIsMissing()) {
                    s_attrArray[j].update((int) insi.value(j));
                    s_attrPairsArray[j].update((int) insi.value(j), (int) insi.classValue());
                }
            }
            if (!insi.classIsMissing()) {
                s_attrArray[s_numOfAttribute - 1].update((int) insi.classValue());

                //**************************************************************
                for (int j = i + 1; j < s_numOfInstances; j++) {
                    Instance insj = s_instances.instance(j);
                    s_HanminDistance[i][j] = hanDistance(insi, insj);
                    s_EuclideanDistance[i][j] = euDistance(insi, insj);
                }
                //**************************************************************
            }
        }
    }

    public double hanDistance(Instance insi, Instance insj) {
        double distance = 0;
        for (int i = 0; i < s_numOfAttribute - 1; i++) {
            if (!insi.isMissing(i) && !insj.isMissing(i)) {
                if (insi.value(i) != insj.value(i)) {
                    distance = distance + 1;
                }
            }
        }
        return distance;
    }

    public double euDistance(Instance insi, Instance insj){
        double distance = 0;
        for(int i = 0; i < s_numOfAttribute-1; i++){
            if (!insi.isMissing(i) && !insj.isMissing(i)){
                distance = distance + (insi.value(i)-insj.value(i))*(insi.value(i)-insj.value(i));
            }
        }
        return fourDecimal(Math.sqrt(distance));
    }

    public double fourDecimal(double d) {
        return Math.floor(d * 10000 + 0.5) / 10000;
    }

    public void computeEntroAndDistance(){
        initialParameters();
        readInstances();

        double hxmean = 0;
        for(int i = 0; i < s_attrArray.length; i++){
            s_attrArray[i].computeEntro();
            hxmean = hxmean + s_attrArray[i].s_entro;
//            System.out.println(s_attrArray[i].s_attrName+"\t"+s_attrArray[i].s_entro);
        }

        s_classEntro = s_attrArray[s_numOfAttribute-1].s_entro;
        hxmean = hxmean - s_classEntro;
        hxmean = fourDecimal(hxmean / (s_numOfAttribute -1));

        double condEntroMean = 0;
        for(int i = 0; i < s_attrPairsArray.length; i++){
            s_attrPairsArray[i].computeEntro();
            s_attrPairsArray[i].computeMultualInfor();
            s_conditionEntro[i] =  s_attrPairsArray[i].s_condEntroi;
            s_multualInfors[i] = s_attrPairsArray[i].s_mulInfor;
            condEntroMean = condEntroMean + s_multualInfors[i];
//            System.out.println(s_attrPairsArray[i].s_attri.s_attrName+"\t"+s_attrPairsArray[i].s_entro+"\t"+s_conditionEntro[i]);
//            System.out.println(s_attrPairsArray[i].s_attri.s_attrName+
//                    "\t"+s_attrPairsArray[i].s_attri.s_entro+
//                    "\t"+s_attrPairsArray[i].s_attrj.s_attrName+
//                    "\t"+s_attrPairsArray[i].s_attrj.s_entro+"\t"
//                    +s_attrPairsArray[i].s_condEntroi+"\t"
//                    +s_attrPairsArray[i].s_entro);
        }

        condEntroMean = fourDecimal(condEntroMean /(s_numOfAttribute - 1));

        s_ratioOfInsAndAttr = fourDecimal(((double) s_numOfInstances)/s_numOfAttribute);
        s_ratioOfIN = fourDecimal((hxmean - condEntroMean)/condEntroMean);
    }

    public double[] getOneItemSets() {
        Vector vecTemp = new Vector();
        for(int i = 0; i < s_attrArray.length; i++){
            singleAttribute attri = s_attrArray[i];
            double []counts = attri.s_countArray;
            for(int j = 0; j < counts.length; j++){
                if(counts[j]>0){
                    vecTemp.addElement(counts[j]);
                }
            }
        }

        double[] oneItemSet = new double[vecTemp.size()];
        for(int i = 0; i < oneItemSet.length; i++){
            oneItemSet[i] = Double.parseDouble(vecTemp.elementAt(i).toString());
            oneItemSet[i] = fourDecimal(oneItemSet[i]/s_numOfInstances);
        }
        
        return oneItemSet;
    }

    public double[] getTwoItemSets(){
        Vector vecTemp = new Vector();
        for(int i = 0; i < s_attrPairsArray.length; i++){
            AttributePairs attrpair = s_attrPairsArray[i];
            double [][]counts = attrpair.s_countArray;
            for(int j = 0; j < counts.length; j++){
                for(int k = 0; k < counts[j].length; k++){
                    if(counts[j][k]>0){
                        vecTemp.addElement(counts[j][k]);
                    }
                }
            }
        }

        double[] twoItemSets = new double[vecTemp.size()];
        for(int i = 0; i < twoItemSets.length; i++){
            twoItemSets[i] = Double.parseDouble(vecTemp.elementAt(i).toString());
            twoItemSets[i] = fourDecimal(twoItemSets[i]/s_numOfInstances);
        }

        return twoItemSets;
    }

    public double[] getClassLabelDistribution(){
        Vector vecTemp = new Vector();
        singleAttribute classLabel = s_attrArray[s_numOfAttribute - 1];
        double[] counts = classLabel.s_countArray;
        for(int i = 0; i < counts.length; i++){
            if(counts[i] > 0){
                vecTemp.addElement(counts[i]);
            }
        }

        double[] targetDistribution = new double[vecTemp.size()];
        for(int i = 0; i < targetDistribution.length; i++){
            targetDistribution[i] = Double.parseDouble(vecTemp.elementAt(i).toString());
            targetDistribution[i] = fourDecimal(targetDistribution[i]/s_numOfInstances);
        }

        return targetDistribution;
    }

    public double[] getMultualInfors(){
        return s_multualInfors;
    }

    public double getClassEntro(){
        return s_classEntro;
    }

    public double getRatioOfInsToAttr(){
        return s_ratioOfInsAndAttr;
    }

    public double getRatioOfInforToNoise(){
        return s_ratioOfIN;
    }

    /**
     * bubble sort
     * @param array
     * @return
     */
//    public double[] sort(double []array){
//        double[] reArray = new double[array.length];
//        for(int i = 0; i < reArray.length; i++){
//            reArray[i] = array[i];
//        }
//
//        int len = reArray.length;
//        int i = len-1;
//        int j = 0;
//
//        boolean flag = false;
//        while(i > 0){
//            flag = false;
//            for(j = 0;j < i; j++){
//                if(reArray[j] > reArray[j+1]){
//                    double temp = reArray[j];
//                    reArray[j] = reArray[j+1];
//                    reArray[j+1] = temp;
//                    flag = true;
//                }
//            }
//
//            if(flag){
//                i--;
//            }else{
//                break;
//            }
//        }
//        return reArray;
//    }

//    public static void main(String []args) throws IOException{
////        String name = "C:\\Documents and Settings\\String\\桌面\\weather.arff";
//////        File file = new File(name);
//////
//////        ArffLoader arffLoader = new ArffLoader();
//////        arffLoader.setFile(file);
////        Instances data = null;
//
//
//        distanceMatrix dm = new distanceMatrix();
//
//        double[] array = {0.2238,0.2575,0.4733,0.5678,0.3371,0.7482,0.4427,0.8001,0.1450,0.2400};
//        double[] reA = dm.sort(array);
//
//        for(int i = 0; i < reA.length; i++){
//            System.out.println(reA[i]);
//        }
////        dm.initialParameters();
////        dm.readInstances();
//    }
}