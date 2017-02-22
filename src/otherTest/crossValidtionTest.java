/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package otherTest;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 *
 * @author Administrator
 */
public class crossValidtionTest {

    public static void main(String[] args) throws IOException{
        String dataName = "C:\\Users\\Administrator\\Desktop\\mulanAcc.arff";
        int randSeed = 15;
        Random rand = new Random(randSeed);
        
        Instances data = readArffDataSet(dataName);
//        double[][] dist = classDistribution(data);
//        showDistribution(dist);
        int N = data.numAttributes();
        for(int i = 0; i < 1; i++){
            crossPartition(data,10,rand,i);
        }
    }

    public static void crossPartition(Instances data, int numOfFolds, Random random, int classIndex){
        data = new Instances(data);
        data.randomize(random);
        data.setClassIndex(classIndex);
        if (data.classAttribute().isNominal()) {
            data.stratify(numOfFolds);
        }

        for (int i = 0; i < numOfFolds; i++) {
            Instances train = data.trainCV(numOfFolds, i, random);
            Instances test = data.testCV(numOfFolds, i);
            double[][] trainDis = classDistribution(train);
            showDistribution(trainDis);
            double[][] testDist = classDistribution(test);
            showDistribution(testDist);
        }
    }

    public static double[][] classDistribution(Instances data){
        int numOfFeatures = data.numAttributes();
        double[][] distributions = new double[numOfFeatures][];
        for(int i = 0; i < numOfFeatures; i++){
            AttributeStats attrStats = data.attributeStats(i);
            distributions[i] = new double[attrStats.distinctCount];
            for(int j = 0; j < distributions[i].length; j++){
                distributions[i][j] = attrStats.nominalCounts[j];
            }
        }
        return distributions;
    }


    public static Instances readArffDataSet(String fileName) throws IOException{
        ArffLoader arffLoader = new ArffLoader();
        File file = new File(fileName);

        arffLoader.setFile(file);
        Instances data = arffLoader.getDataSet();

        return data;
    }

    public static void showDistribution(double[][] dist){
        for(int i = 0; i < dist.length; i++){
            for(int j = 0; j < dist[i].length; j++){
                System.out.print(dist[i][j]+",");
            }
            System.out.println();
        }
    }
}
