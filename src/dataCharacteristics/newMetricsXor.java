/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataCharacteristics;

import fileUtil.fileHandle;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;

/**
 * Refference paper: "Distances between Data Sets Based on Summary Statistics"
 * 
 * @author String
 */
public class newMetricsXor extends dataProcess {

	protected double[] s_freqOneItem; // the frequency of the one item sets
	protected double[] s_freqTwoItems; // the frequency of the two item sets

	/**
	 * The statistical information of the frequency sequence of one item sets
	 */
	protected double s_minOne;
	protected double s_lowQuartOne;
	protected double s_medOne;
	protected double s_uppQuartOne;
	protected double s_maxOne;
	protected double s_meanOne;
	protected double s_stdOne;
	protected double s_skewOne;
	protected double s_turOne;

	/**
	 * The statistical information of the frequency sequence of two item sets
	 */
	protected double s_minTwo;
	protected double s_lowQuartTwo;
	protected double s_medTwo;
	protected double s_uppQuartTwo;
	protected double s_maxTwo;
	protected double s_meanTwo;
	protected double s_stdTwo;
	protected double s_skewTwo;
	protected double s_turTwo;

	protected double s_runtime;

	public newMetricsXor() {
		super();
	}

	public newMetricsXor(String fileName) throws Exception {
		super(fileName);
		readDataSet();
	}

	public void setFreqOfOneItem() {
		Vector<Integer> vec = new Vector<Integer>();
		int numOfFeatures = s_dataset.numAttributes() - 1;

		for (int i = 0; i < numOfFeatures; i++) {
			double[] counts = countFeaAlone(i);
			for (int j = 0; j < counts.length; j++) {
				int count = (int) counts[j];
				vec.addElement(count);
			}
		}

		s_freqOneItem = new double[vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			s_freqOneItem[i] = Double.parseDouble(vec.elementAt(i).toString()) / s_dataset.numInstances();
		}

		vec.removeAllElements();
		vec.trimToSize();
		vec = null;
	}

	public void setFreqOfTwoItems() {
		Vector<Integer> vec = new Vector<Integer>();
		int numOfFeatures = s_dataset.numAttributes() - 1;

		for (int i = 0; i < numOfFeatures; i++) {
			double[][] counts = countXorFeaPairs(i);
			for (int j = 0; j < counts.length; j++) {
				for (int k = 0; k < counts[j].length; k++) {
					int count = (int) counts[j][k];
					if (count > 0) {
						vec.addElement(count);
					}
				}
			}
		}

		s_freqTwoItems = new double[vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			s_freqTwoItems[i] = Double.parseDouble(vec.elementAt(i).toString())
					/ s_dataset.numInstances();
		}

		vec.removeAllElements();
		vec.trimToSize();
		vec = null;
	}

	public double[] sort(double[] array) {
		double[] reArray = new double[array.length];
		for (int i = 0; i < reArray.length; i++) {
			reArray[i] = array[i];
		}

		int len = reArray.length;
		int i = len - 1;
		int j = 0;

		boolean flag = false;
		while (i > 0) {
			flag = false;
			for (j = 0; j < i; j++) {
				if (reArray[j] > reArray[j + 1]) {
					double temp = reArray[j];
					reArray[j] = reArray[j + 1];
					reArray[j + 1] = temp;
					flag = true;
				}
			}

			if (flag) {
				i--;
			} else {
				break;
			}
		}
		return reArray;
	}

	public double[] arraySummary(double[] array) {
		double[] sortArray = sort(array);// Sort the given array in increasing
											// order
		int size = array.length;

		double min = sortArray[0]; // Minimum value of the given array
		double max = sortArray[size - 1];// Maximum value of the given array
		double med = 0; // Median value of the given array

		int medIndex = (size + 1) / 2;

		if (size % 2 == 1) {
			med = sortArray[medIndex - 1];
		} else {
			med = fourDecimal((sortArray[medIndex - 1] + sortArray[medIndex]) / 2);
		}

		int quartIndex = (size + 1) / 4;
		int fracCount = (size + 1) % 4;
		double quart = 0; // lower quartile
		double quart3 = 0; // upper quartile

		if (fracCount == 0) {
			quart = sortArray[quartIndex];
			quart3 = sortArray[size - quartIndex - 1];
		} else {
			quart = fourDecimal((sortArray[quartIndex] + (sortArray[quartIndex + 1] - sortArray[quartIndex])
					* (fracCount / 4.0)));
			quart3 = fourDecimal((sortArray[size - quartIndex - 1] - (sortArray[size
					- quartIndex - 1] - sortArray[size - quartIndex - 2])
					* (fracCount / 4.0)));
		}

		// Five summary 1: min 2: lower quartile 3: median 4: upper quartile
		// 5:max
		// ==============================================================================

		double interquart = fourDecimal(quart3 - quart);
		// 6: The interquartile range
		// ==============================================================================

		double mean = 0; // Mean value of the given array
		double harmMean = 0;
		double Var = 0; // Variance of the given array
		double Std = 0; // Standard deviation
		double Kurtosis = 0;
		double Skewness = 0;
		double Mad = 0;

		for (int i = 0; i < size; i++) {
			if (array[i] != 0) {
				mean = mean + array[i];
				harmMean = harmMean + 1 / array[i];
			}
		}
		mean = fourDecimal(mean / size);
		harmMean = fourDecimal(size / harmMean);

		double temp = 0;
		double abs = 0;
		double x2 = 0;
		double x3 = 0;
		double x4 = 0;

		for (int i = 0; i < array.length; i++) {
			abs = abs + (Math.abs(array[i] - mean));
			temp = ((array[i] - mean) * (array[i] - mean));
			x2 = (x2 + temp);
			temp = (temp * (array[i] - mean));
			x3 = (x3 + temp);
			temp = (temp * (array[i] - mean));
			x4 = (x4 + temp);
		}

		Mad = fourDecimal(abs / size);
		Var = fourDecimal(x2 / size);
		Std = fourDecimal(Math.sqrt(x2 / (size - 1)));

		// temp = fourDecimal(x2 / size);
		// Skewness = fourDecimal(x3/size);
		if (Var > 0) {
			Skewness = fourDecimal((x3 / size) / (Math.pow(Var, 1.5)));
			Kurtosis = fourDecimal((x4 / size) / (Var * Var));
		}

		// Statistical measures: 7: mean 8: harmonic mean 9: variance 10:
		// standard deviation
		// 11: skrewness 12: kurtosis 13: mean of absolute deviation
		// ==============================================================================

		double[] summary = new double[13];
		summary[0] = min;
		summary[1] = quart;
		summary[2] = med;
		summary[3] = quart3;
		summary[4] = max;
		summary[5] = interquart;
		summary[6] = mean;
		summary[7] = harmMean;
		summary[8] = Var;
		summary[9] = Std;
		summary[10] = Skewness;
		summary[11] = Kurtosis;
		summary[12] = Mad;
		return summary;
	}

	public double fourDecimal(double d) {
		return Math.floor(d * 10000 + 0.5) / 10000;
	}

	public void computeMetrics() {
		long startTime = System.nanoTime();
		setFreqOfOneItem();
		setFreqOfTwoItems();

		double[] ones = arraySummary(s_freqOneItem);
		double[] twos = arraySummary(s_freqTwoItems);

		s_minOne = fourDecimal(ones[0]);
		s_lowQuartOne = fourDecimal(ones[1]);
		s_medOne = fourDecimal(ones[2]);
		s_uppQuartOne = fourDecimal(ones[3]);
		s_maxOne = fourDecimal(ones[4]);

		s_meanOne = fourDecimal(ones[6]);
		s_stdOne = fourDecimal(ones[9]);
		s_skewOne = fourDecimal(ones[10]);
		s_turOne = fourDecimal(ones[11]);

		s_minTwo = fourDecimal(twos[0]);
		s_lowQuartTwo = fourDecimal(twos[1]);
		s_medTwo = fourDecimal(twos[2]);
		s_uppQuartTwo = fourDecimal(twos[3]);
		s_maxTwo = fourDecimal(twos[4]);

		s_meanTwo = fourDecimal(twos[6]);
		s_stdTwo = fourDecimal(twos[9]);
		s_skewTwo = fourDecimal(twos[10]);
		s_turTwo = fourDecimal(twos[11]);
		long endTime = System.nanoTime();

		s_runtime = fourDecimal((endTime - startTime) / 1000000.0);
	}

	public String outputFeaVector() {
		StringBuffer sb = new StringBuffer();
		sb.append(s_minOne + "," + s_lowQuartOne + "," + s_medOne + ","
				+ s_uppQuartOne + "," + s_maxOne + "," + s_meanOne + ","
				+ s_stdOne + "," + s_skewOne + "," + s_turOne + ",");
		sb.append(s_minTwo + "," + s_lowQuartTwo + "," + s_medTwo + ","
				+ s_uppQuartTwo + "," + s_maxTwo + "," + s_meanTwo + ","
				+ s_stdTwo + "," + s_skewTwo + "," + s_turTwo);
		return sb.toString();
	}

	public double[] AchieveMetrics() {
		double[] metrics = new double[18];
		metrics[0] = s_minOne;
		metrics[1] = s_lowQuartOne;
		metrics[2] = s_medOne;
		metrics[3] = s_uppQuartOne;
		metrics[4] = s_maxOne;
		metrics[5] = s_meanOne;
		metrics[6] = s_stdOne;
		metrics[7] = s_skewOne;
		metrics[8] = s_turOne;

		metrics[9] = s_minTwo;
		metrics[10] = s_lowQuartTwo;
		metrics[11] = s_medTwo;
		metrics[12] = s_uppQuartTwo;
		metrics[13] = s_maxTwo;
		metrics[14] = s_meanTwo;
		metrics[15] = s_stdTwo;
		metrics[16] = s_skewTwo;
		metrics[17] = s_turTwo;

		return metrics;
	}

	public static void main(String[] args) throws Exception {
		
		//将输出结果保存到文件
	
//			FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\sample-result\\dataCharacteristic\\oidsData\\newMetricsXor.csv",true);
//			PrintStream ps = new PrintStream(out);
//			System.setOut(ps);
			
		// String dataFilePath =
		// "E:\\My works\\Experiment results\\Algorithm Recommendation\\DR data set\\";
		// String fileNamePath =
		// "E:\\My works\\Experiment results\\Algorithm Recommendation\\Novel data set characteristics\\accuracy\\";
		String filePath = "E:\\experiment\\dataset\\UCI\\dataSetOids\\test\\";
		fileHandle fh = new fileHandle();
		String[] fileNames = fh.getFileNames(filePath, "arff", true);
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			newMetricsXor newmetricsXor = new newMetricsXor(filePath + fileName+ ".arff");
			if (newmetricsXor.s_isOnlyClass) {
				continue;
			}

			newmetricsXor.computeMetrics();
			String line = newmetricsXor.outputFeaVector();
			System.out.println((i + 1) + "," + fileName + "," + line);
		}

		// String fileName =
		// "C:\\Documents and Settings\\String\\濡楀矂娼癨\weather.arff";
		// newMetricsXor newmetricsXor = new newMetricsXor(fileName);
		// newmetricsXor.computeMetrics();
		// String line = newmetricsXor.outputFeaVector();
		// System.out.println(line);
	}
}
