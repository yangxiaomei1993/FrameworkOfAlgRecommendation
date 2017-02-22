/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataCharacteristics;

//import dataCharacteristics.dataProcess;

import DecisionTree.ClassifierTree;
import DecisionTree.J48;
import fileUtil.fileHandle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * Construct induced Decision Tree Extract the information of the tree as the
 * meta-features Number of leaves Number of nodes Width of the tree and Depth of
 * the tree
 * 
 * @author String
 */
public class ModelBasedMetrics {

	public static J48 s_TreeStructure;
	public static double[] s_NodeInLevel;
	public static double[] s_AttrNoUsed;
	public static FastVector s_LenOfBranch;

	public static int s_numOfAttrs;
	public static HashMap<String, Integer> s_attrNames;
	public static double s_runtime;

	/**
	 * The fifteen metrics extracted from the Induced Decision Tree
	 */
	protected static double s_treeWidth;// 1
	protected static double s_treeHeight;// 2
	protected static double s_numOfNodes;// 3
	protected static double s_numOfLeaves;// 4

	protected static double s_maxLevel;// 5
	protected static double s_meanLevel;// 6
	protected static double s_devLevel;// 7

	protected static double s_longBranch;// 8
	protected static double s_shortBranch;// 9
	protected static double s_meanBranch;// 10
	protected static double s_devBranch;// 11

	protected static double s_maxAttr;// 12
	protected static double s_minAttr;// 13
	protected static double s_meanAttr;// 14
	protected static double s_devAttr;// 15
	String fileName = "";

	public static void ConstructDecitionTree(Instances data) throws Exception {
		Instances dataset = new Instances(data, 0, data.numInstances());
		s_TreeStructure = new J48();
		s_TreeStructure.buildClassifier(dataset);
	}

	// public ModelBasedMetrics(String fileName) throws Exception{
	// super(fileName);
	// readDataSet();
	// }

	public static Instances setArffData(Instances data) {
		data.deleteWithMissingClass();
		s_numOfAttrs = data.numAttributes() - 1;
		s_attrNames = new HashMap<String, Integer>(s_numOfAttrs);

		for (int i = 0; i < s_numOfAttrs; i++) {
			String attrName = data.attribute(i).name();
			s_attrNames.put(attrName, i);
		}

		return data;
	}

	public static Instances readArffData(String fileName) throws IOException {
		File file = new File(fileName);
		ArffLoader arffLoader = new ArffLoader();
		arffLoader.setFile(file);

		Instances data = arffLoader.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);
		data.deleteWithMissingClass();

		s_numOfAttrs = data.numAttributes() - 1;
		s_attrNames = new HashMap<String, Integer>(s_numOfAttrs);

		for (int i = 0; i < s_numOfAttrs; i++) {
			String attrName = data.attribute(i).name();
			s_attrNames.put(attrName, i);
		}
		return data;
	}

	public static void modelMetricCollection() throws Exception {

		long startTime = System.nanoTime();
		ClassifierTree root = s_TreeStructure.getRoot();
		// root.assignIDs(-1);
		root.assignLevels(-1);
		s_treeHeight = root.treeDepth() + 1;
		s_numOfNodes = root.numNodes();
		s_numOfLeaves = root.numLeaves();
		s_treeWidth = s_numOfLeaves - 1;

		s_NodeInLevel = new double[(int) s_treeHeight];
		for (int i = 0; i < s_NodeInLevel.length; i++) {
			s_NodeInLevel[i] = 0;
		}
		s_AttrNoUsed = new double[s_numOfAttrs];
		for (int i = 0; i < s_AttrNoUsed.length; i++) {
			s_AttrNoUsed[i] = 0;
		}
		s_LenOfBranch = new FastVector();

		scanClassifierTree(root);
		aquireLevelMetrics();
		aquireBranchMetrics();
		aquireAttrMetrics();

		long runtime = System.nanoTime() - startTime;

		s_runtime = fourDecimal(runtime / 1000000.0);
		// System.out.println(s_numOfNodes + "\t" +s_numOfLeaves + "\t" +
		// s_treeHeight+ "\t" +
		// root.getLocalModel().leftSide(root.getTrainData()));
		// for(int i = 0; i < s_NodeInLevel.length; i++){
		// System.out.println(s_NodeInLevel[i]);
		// }
		// System.out.println("===========================================");
		// for(int i = 0; i < s_AttrNoUsed.length; i++){
		// System.out.println(s_AttrNoUsed[i]);
		// }
	}

	public static String showMetrics() {
		StringBuffer sb = new StringBuffer();
		sb.append(s_treeWidth + ",");
		sb.append(s_treeHeight + ",");
		sb.append(s_numOfNodes + ",");
		sb.append(s_numOfLeaves + ",");

		sb.append(s_maxLevel + "," + s_meanLevel + "," + s_devLevel + ",");
		sb.append(s_longBranch + "," + s_shortBranch + "," + s_meanBranch + ","
				+ s_devBranch + ",");
		sb.append(s_maxAttr + "," + s_minAttr + "," + s_meanAttr + ","
				+ s_devAttr);

		return sb.toString();
	}

	public static double[] CollectMetrics() {
		double[] metrics = new double[15];
		metrics[0] = s_treeWidth;
		metrics[1] = s_treeHeight;
		metrics[2] = s_numOfNodes;
		metrics[3] = s_numOfLeaves;

		metrics[4] = s_maxLevel;
		metrics[5] = s_meanLevel;
		metrics[6] = s_devLevel;
		metrics[7] = s_longBranch;
		metrics[8] = s_shortBranch;
		metrics[9] = s_meanBranch;
		metrics[10] = s_devBranch;
		metrics[11] = s_maxAttr;
		metrics[12] = s_minAttr;
		metrics[13] = s_meanAttr;
		metrics[14] = s_devAttr;
		return metrics;
	}

	public static void aquireLevelMetrics() {
		s_maxLevel = Double.NEGATIVE_INFINITY;
		double x = 0;
		double x2 = 0;
		for (int i = 0; i < s_NodeInLevel.length; i++) {
			x = x + s_NodeInLevel[i];
			x2 = x2 + s_NodeInLevel[i] * s_NodeInLevel[i];
			if (s_NodeInLevel[i] > s_maxLevel) {
				s_maxLevel = s_NodeInLevel[i];
			}
		}

		s_meanLevel = fourDecimal(x / s_NodeInLevel.length);

		double var = 0;
		if (s_NodeInLevel.length > 1) {
			var = (x2 / s_NodeInLevel.length - s_meanLevel * s_meanLevel)
					* s_NodeInLevel.length / (s_NodeInLevel.length - 1.0);
		}
		s_devLevel = fourDecimal(Math.sqrt(var));

		if ((s_meanLevel == 0) && (s_devLevel == 0)) {
			s_maxLevel = 0;
		}
	}

	public static void aquireBranchMetrics() {
		s_longBranch = Double.NEGATIVE_INFINITY;
		s_shortBranch = Double.POSITIVE_INFINITY;

		double x = 0;
		double x2 = 0;

		for (int i = 0; i < s_LenOfBranch.size(); i++) {
			double temp = Double.parseDouble(s_LenOfBranch.elementAt(i)
					.toString());

			if (temp > s_longBranch) {
				s_longBranch = temp;
			}

			if (temp < s_shortBranch) {
				s_shortBranch = temp;
			}

			x = x + temp;
			x2 = x2 + temp * temp;
		}

		s_meanBranch = fourDecimal(x / s_LenOfBranch.size());

		double var = 0;
		if (s_LenOfBranch.size() > 1) {
			var = (x2 / s_LenOfBranch.size() - s_meanBranch * s_meanBranch)
					* s_LenOfBranch.size() / (s_LenOfBranch.size() - 1.0);
		}
		s_devBranch = fourDecimal(Math.sqrt(var));

		if ((s_meanBranch == 0) && (s_devBranch == 0)) {
			s_longBranch = s_shortBranch = 0;
		}
	}

	public static void aquireAttrMetrics() {
		s_maxAttr = Double.NEGATIVE_INFINITY;
		s_minAttr = Double.POSITIVE_INFINITY;

		double x = 0;
		double x2 = 0;
		double count = 0;
		for (int i = 0; i < s_AttrNoUsed.length; i++) {
			if (s_AttrNoUsed[i] > 0) {
				count++;
				x = x + s_AttrNoUsed[i];
				x2 = x2 + s_AttrNoUsed[i] * s_AttrNoUsed[i];

				if (s_maxAttr < s_AttrNoUsed[i]) {
					s_maxAttr = s_AttrNoUsed[i];
				}

				if (s_minAttr > s_AttrNoUsed[i]) {
					s_minAttr = s_AttrNoUsed[i];
				}
			}
		}

		if (count > 0) {
			s_meanAttr = fourDecimal(x / count);
		} else {
			s_meanAttr = 0;
		}

		double var = 0;
		if (count > 1) {
			var = (x2 / count - s_meanAttr * s_meanAttr) * count / (count - 1);
		}
		s_devAttr = fourDecimal(Math.sqrt(var));

		if ((s_meanAttr == 0) && (s_devAttr == 0)) {
			s_maxAttr = s_minAttr = 0;
		}
	}

	public static double fourDecimal(double d) {
		return Math.floor(d * 10000 + 0.5) / 10000;
	}

	public static void scanClassifierTree(ClassifierTree tree) {
		int treeLevel = tree.getTreeLevel();
		s_NodeInLevel[treeLevel]++;

		if (tree.getIsLeafFlag()) {
			s_LenOfBranch.addElement(treeLevel);
			return;
		} else {
			String splitAttrName = tree.getLocalModel().leftSide(
					tree.getTrainData());
			int splitAttrIndex = Integer.parseInt(s_attrNames
					.get(splitAttrName).toString());
			s_AttrNoUsed[splitAttrIndex]++;

			ClassifierTree[] sons = tree.getSons();
			for (int i = 0; i < sons.length; i++) {
				scanClassifierTree(sons[i]);
			}
		}
	}

	public static void test() throws Exception {
		System.out.println(s_TreeStructure.getRoot().graph());
	}

	public static void performModel(String filename) throws Exception {


		Instances data = readArffData(filename);
		long startTime = System.nanoTime();
		ConstructDecitionTree(data);
		modelMetricCollection();
		long endTime = System.nanoTime();
		double runtime = (endTime - startTime) / 1000000.0;
		//System.out.println(filename + "\n" + runtime);
		System.out.println(filename + "," + showMetrics());
	}

	// return CollectMetrics();

	public static void main(String[] args) throws IOException, Exception {
		// String dataFilePath = "E:\\My works\\Experiment results\\"
		// +
		// "Frameworks of Algorithm Recommendation\\UCI Data Sets\\Classification\\";

		// String dataFilePath = "E:\\My works\\Experiment results\\" +
		// "Frameworks of Algorithm Recommendation\\UCI Data Sets\\" +
		// "All Data sets\\";
		//
		// String dataFilePath = "D:\\My works\\Experiment results\\"
		// +
		// "Classification Algorithm Recommendation by Clustering\\Data set\\Orig data sets\\";
		// // String fileNamePath =
		// "E:\\My works\\Experiment results\\Algorithm Recommendation\\Classification Results\\accuracy\\";
		//
		// fileHandle fh = new fileHandle();
		// String[] fileNames = fh.getFileNames(dataFilePath, "arff",true);
		//
		// for(int i = 0; i < fileNames.length; i++){
		// String fileName = fileNames[i];
		//
		// // System.out.println(fileName);
		// long startTime = System.nanoTime();
		// ModelBasedMetrics ModelMetrics = new
		// ModelMetrics(dataFilePath+fileName+".arff");
		// ModelMetrics.CollectMetrics();
		// String line = tradMetrics.outputFeaVector();
		// long endTime = System.nanoTime();
		//
		// double runtime = (endTime - startTime)/1000000.0;
		// // System.out.println((i+1)+","+fileNames[i]+","+"\t"+runtime);
		// System.out.println((i+1)+","+fileNames[i]+","+line);
		
//		FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\sample-result\\dataCharacteristic\\oidsData\\modelBasedMetrics.csv",true);
//		PrintStream ps = new PrintStream(out);
//		System.setOut(ps);
		
		String dataFilePath = "E:\\experiment\\dataset\\UCI\\dataSetOids\\test\\";

		// String fileName = "";
		fileHandle fh = new fileHandle();
		String[] fileNames = fh.getFileNames(dataFilePath, "arff");
		for (int i = 0; i < fileNames.length; i++) {
			// fileName = fileNames[i];
			// // String data = filenmes[i];
			//ModelBasedMetrics mymodel = new ModelBasedMetrics();
			System.out.print((i+1)+",");
			ModelBasedMetrics.performModel(dataFilePath + fileNames[i]);
		}
		// for(int k=0;k<16;k++){
		// System.out.println(kk[k]);
		// }
		// for (int i = 0; i < fileNames.length; i++) {
		// fileName = fileNames[i];
		// Instances data = readArffData(dataFilePath + fileName);
		// long startTime = System.nanoTime();
		// ConstructDecitionTree(data);
		// modelMetricCollection();
		// long endTime = System.nanoTime();
		// double runtime = (endTime - startTime)/1000000.0;
		// System.out.println(i+","+ fileNames[i] + "," + runtime);
		// System.out.println(fileName + "," + showMetrics());
		//
		// }
	}
}
