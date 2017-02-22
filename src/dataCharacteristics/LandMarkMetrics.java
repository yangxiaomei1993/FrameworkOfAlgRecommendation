/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataCharacteristics;

import fileUtil.fileHandle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IB1;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

/**
 * We choose five simple classifiers as the landmarkers The idea is from the
 * papers: "Meta-Learning by Landmarking Various Learning Algorithms" and ""
 * Naive Bayes IB1 Best tree node Worst tree node Random tree node
 * 
 * Landmark values themselves were determined by tenfold stratied
 * cross-validation.
 * 
 * @author String
 */
public class LandMarkMetrics {

	public static Classifier[] LandMarkers;
	public static double[] landMarkMetrics;
	public static double runtime;

	public static void setLandMarker() {
		LandMarkers = new Classifier[5];
		LandMarkers[0] = new NaiveBayes();
		LandMarkers[1] = new IB1();
		SimpleDecisionTree bestNode = new SimpleDecisionTree();
		bestNode.setMode(0);
		LandMarkers[2] = bestNode;
		SimpleDecisionTree worstNode = new SimpleDecisionTree();
		worstNode.setMode(1);
		LandMarkers[3] = worstNode;
		SimpleDecisionTree randNode = new SimpleDecisionTree();
		randNode.setMode(2);
		LandMarkers[4] = randNode;
	}

	public static double classPerformance(Classifier classifier, Instances data)throws Exception {
		double sum = 0;
		int runPass = 1;
		for (int i = 1; i <= runPass; i++) {
			Evaluation evaluation = new Evaluation(data);
			int randomSeed = i;
			Random rand = new Random(randomSeed);
			evaluation.crossValidateModel(classifier, data, 10, rand);
			// System.out.println(evaluation.correct()+"\t"+evaluation.toSummaryString());
			// System.out.println(evaluation.pctCorrect());
			sum = sum + evaluation.pctCorrect();
		}
		// System.out.println(sum/runPass);
		return (sum / runPass);
	}

	public static double[] achieveMetrics(Instances data) throws Exception {
		setLandMarker();

		long startTime1 = System.nanoTime();
		landMarkMetrics = new double[LandMarkers.length];
		for (int i = 0; i < 2; i++) {
			landMarkMetrics[i] = classPerformance(LandMarkers[i], data);
		}
		long endTime1 = System.nanoTime();
		if (isContainNumeric(data)) {
			data = discretization(data);
		}

		long startTime2 = System.nanoTime();
		for (int i = 2; i < landMarkMetrics.length; i++) {
			landMarkMetrics[i] = classPerformance(LandMarkers[i], data);
		}

		long endTime2 = System.nanoTime();
		double time = endTime1 - startTime1 + endTime2 - startTime2;
		runtime = twoDecimal(time / 1000000);
		return landMarkMetrics;

	}

	public static Instances readArffData(String fileName) throws IOException {
		File file = new File(fileName);
		ArffLoader arffLoader = new ArffLoader();
		arffLoader.setFile(file);

		Instances data = arffLoader.getDataSet();

		data.setClassIndex(data.numAttributes() - 1);

		data.deleteWithMissingClass();
		return data;
	}

	protected static boolean isContainNumeric(Instances data) {
		for (int i = 0; i < data.numAttributes() - 1; i++) {
			if (data.attribute(i).isNumeric()) {
				return true;
			}
		}
		return false;
	}

	protected static Instances discretization(Instances data) throws Exception {
		Discretize filter = new Discretize();
		filter.setInputFormat(data);
        //data.classAttribute().isNumeric()
		Instances disData = Filter.useFilter(data, filter);

		return disData;
	}

	protected static double twoDecimal(double d) {
		return Math.floor(d * 10000 + 0.005) / 10000;
	}

	public static String metricLine() {
		StringBuffer sb = new StringBuffer();
		sb.append(twoDecimal(landMarkMetrics[0]));
		for (int i = 1; i < landMarkMetrics.length; i++) {
			sb.append("," + twoDecimal(landMarkMetrics[i]));
		}

//		sb.append("," + runtime);
		return sb.toString();
	}

	public static double[] achieveMetrics() {
		return landMarkMetrics;
	}

	public static void performLandMarkMetrics(String filename) throws Exception {
		// System.out.print((i + 1) + ". " + fileNames[i]);
		Instances data = readArffData(filename);
		achieveMetrics(data);
		//System.out.println(filename);
		System.out.println(metricLine());
	}

	public static void main(String[] args) throws IOException, Exception {
		
//		FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\result\\dataCharacteristic\\oidsData\\landMarkMetrics.csv",true);
//		PrintStream ps = new PrintStream(out);
//		System.setOut(ps);

		String dataFilePath = "E:\\experiment\\dataset\\UCI\\dataSetOids\\test\\";
		fileHandle fh = new fileHandle();
		String[] fileNames = fh.getFileNames(dataFilePath,"arff",true);
		// double []arr = null;
		for (int i = 0; i < fileNames.length; i++) {
	//	for (int i = 0; i < 1; i++) {
			// System.out.print((i + 1) + ". " + fileNames[i]);
		//	LandMarkMetrics myLandMarkMetrics = new LandMarkMetrics();
			System.out.print((i + 1) + "," + fileNames[i] + ",");
			LandMarkMetrics.performLandMarkMetrics(dataFilePath +"\\"+ fileNames[i]+".arff");
			// Instances data = readArffData(dataFilePath + fileNames[i] +
			// ".arff");
			// achieveMetrics(data);
			// System.out.println(metricLine());
		}
		// for(int k=0;k< LandMarkers.length;k++)
		// System.out.println(arr[k]);
	}
}
