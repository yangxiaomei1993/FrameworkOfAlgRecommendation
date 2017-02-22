/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataCharacteristics;

import java.io.FileOutputStream;
import java.io.PrintStream;

import fileUtil.fileHandle;
import weka.core.ContingencyTables;

/**
 * Reference paper:
 * "Meta-data: Characterization of Input Features for Meta-learning"
 * 
 * @author String
 */
public class traditionalMetrics extends dataProcess {

	/**
	 * General data set metrics
	 */
	protected static double s_numOfInstances; // number of instances
	protected static double s_numOfFeatures; // number of features
	protected static double s_numOfTargets; // number of the target concept values
	protected static double s_dimensional; // number of features/number of
											// instances

	/**
	 * Information theory based data set metrics
	 */

	/* normalization entropy of the target concept value: H(C)/log(count(C)) */
	protected static double s_normClassEntro;
	/* mean value of the normalization entropies over all the features */
	protected static double s_meanNormFeaEntro;
	/*
	 * mean value of the joint entropy bettween all the features and target
	 * concept
	 */
	protected static double s_meanJointFeaEntro;
	/*
	 * mean value of the multual information between the feature and target
	 * concept over all features
	 */
	protected static double s_meanMI;
	/* The maximum multual information value of the feature and target concept */
	protected static double s_maxMI;
	/*
	 * The ratio of the entropy of the target concept and the mean multual
	 * information
	 */
	protected static double s_equalNumOfFeatures;
	/* The ratio the noisy and the signal */
	protected static double s_NSRatio;

	protected static long runtime;// Runtime to extract the data set metrics

	public traditionalMetrics() {
		super();
	}

	public traditionalMetrics(String fileName) throws Exception {
		super(fileName);
		readDataSet();
	}

	public void setNumOfInstances() {
		s_numOfInstances = s_dataset.numInstances();
	}

	public void setNumOfFeatures() {
		s_numOfFeatures = s_dataset.numAttributes() - 1;
	}
	public void setAttributeClass() {
		s_dataset.setClassIndex(s_dataset.numAttributes() - 1);
	}
	

	public void setNumOfTargets() {
		s_numOfTargets = s_dataset.numClasses();
	}

	public void setDimensional() {
		s_dimensional = fourDecimal(s_numOfFeatures / s_numOfInstances);
	}

	public void setNormClassEntro() {
		double[] counts = countFeaAlone(s_dataset.classIndex());
		if (counts.length != s_numOfTargets) {
			s_numOfTargets = counts.length;
		}
		s_normClassEntro = fourDecimal(ContingencyTables.entropy(counts)
				/ (Math.log(s_numOfTargets) / Math.log(2.0)));
	}

	public void setMeanNormFeaEntro() {
		double sumEntropy = 0;
		for (int i = 0; i < s_numOfFeatures; i++) {
			sumEntropy = sumEntropy + computeNormEntropy(i);
		}

		s_meanNormFeaEntro = fourDecimal(sumEntropy / s_numOfFeatures);
	}

	public void setMeanAndMaxMultalInfors() {
		double sumMI = 0;
		double maxMI = 0;

		for (int i = 0; i < s_numOfFeatures; i++) {
			double mi = computeMI(i);
			sumMI = sumMI + mi;
			if (mi > maxMI) {
				maxMI = mi;
			}
		}

		s_meanMI = fourDecimal(sumMI / s_numOfFeatures);
		s_maxMI = fourDecimal(maxMI);
	}

	public void setEqualNumber() {
		s_equalNumOfFeatures = fourDecimal(s_normClassEntro
				* (Math.log(s_numOfTargets) / Math.log(2)) / s_meanMI);
	}

	public void setNSRatio() {
		double meanEntropy = 0;
		for (int i = 0; i < s_numOfFeatures; i++) {
			meanEntropy = meanEntropy + computeEntropy(i);
		}

		meanEntropy = fourDecimal(meanEntropy / s_numOfFeatures);
		s_NSRatio = fourDecimal((meanEntropy - s_meanMI) / s_meanMI);
	}

	public double computeMI(int feaIndex) {
		double[][] counts = countFeaPairs(feaIndex);
		double[][] redCounts = ContingencyTables.reduceMatrix(counts);

		if (redCounts.length <= 1) {
			return 0;
		}
		// System.out.println(feaIndex);
		double su = ContingencyTables.symmetricalUncertainty(redCounts);
		double hfc = ContingencyTables.entropyOverColumns(redCounts)
				+ ContingencyTables.entropyOverRows(redCounts);

		double mi = su * hfc / 2;
		// System.out.println(feaIndex+"\t"+mi);
		return mi;
	}

	public double computeNormEntropy(int feaIndex) {
		double[] counts = countFeaAlone(feaIndex);
		double countLen = counts.length;

		double normEntropy = fourDecimal(ContingencyTables.entropy(counts)
				/ (Math.log(countLen) / Math.log(2.0)));
		return normEntropy;
	}

	public double computeEntropy(int feaIndex) {
		double[] counts = countFeaAlone(feaIndex);

		double entropy = fourDecimal(ContingencyTables.entropy(counts));
		// System.out.println(feaIndex+"\t"+entropy);
		return entropy;
	}

	// ==========================================================================
	public double fourDecimal(double d) {
		return Math.floor(d * 10000 + 0.5) / 10000;
	}

	public static String outputFeaVector() {
		StringBuffer sb = new StringBuffer();
		sb.append(s_numOfInstances + "," + s_numOfFeatures + ","
				+ s_numOfTargets + "," + s_dimensional + ",");
		sb.append(s_normClassEntro + "," + s_meanNormFeaEntro + "," + s_meanMI
				+ "," + s_maxMI + "," + s_equalNumOfFeatures + "," + s_NSRatio);
		return sb.toString();
	}

	public void computeMetrics() {
		long startTime = System.nanoTime();
		this.setNumOfInstances();
		this.setNumOfFeatures();
		this.setNumOfTargets();
		this.setDimensional();
		this.setNormClassEntro();
		this.setMeanNormFeaEntro();
		this.setMeanAndMaxMultalInfors();
		this.setEqualNumber();
		this.setNSRatio();
		long endTime = System.nanoTime();
		runtime = endTime - startTime;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
//		FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\sample-result\\dataCharacteristic\\oidsData\\traditionalMetrics.csv",true);
//		PrintStream ps = new PrintStream(out);
//		System.setOut(ps);

		// String dataFilePath = "D:\\weka_data";

		String dataFilePath = "E:\\experiment\\dataset\\UCI\\arff\\test\\";
		// String fileNamePath =
		// "E:\\My works\\Experiment results\\Algorithm Recommendation\\Classification Results\\accuracy\\";

		fileHandle fh = new fileHandle();
		String[] fileNames = fh.getFileNames(dataFilePath, "arff", true);

		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];

			// System.out.println(fileName);
			long startTime = System.nanoTime();
			traditionalMetrics tradMetrics = new traditionalMetrics(
					dataFilePath + fileName + ".arff");
			tradMetrics.computeMetrics();
			String line = traditionalMetrics.outputFeaVector();
			long endTime = System.nanoTime();

			double runtime = (endTime - startTime) / 1000000.0;
			// System.out.println((i+1)+","+fileNames[i]+","+"\t"+runtime);
			System.out.println((i + 1) + "," + fileNames[i] + "," + line);
			// System.out.println(line);
		}

		// String fileName =
		// "C:\\Documents and Settings\\String\\妗岄潰\\weather.arff";
		// traditionalMetrics tradMetrics = new traditionalMetrics(fileName);
		// tradMetrics.computeMetrics();
		// String line = tradMetrics.outputFeaVector();
		// System.out.println(line);
	}
}
