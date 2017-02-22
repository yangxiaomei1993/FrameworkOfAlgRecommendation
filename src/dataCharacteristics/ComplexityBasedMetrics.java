/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataCharacteristics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;

import metricMST.rapidMSTree;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IB1;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import fileUtil.fileHandle;

/**
 * 
 * @author String
 */
public class ComplexityBasedMetrics {

	// ==data set
	// information====================================================
	protected Instances s_dataInstances;
	protected int s_numOfInstances; // number of the instances in the data set
	protected int s_numOfAttribute; // number of the features in the data set
	protected Instances[] s_splitDataInstances; // Split the instances into k
												// binary classification
												// problems
	protected int[] s_classValueCount; // Counts of different class values
	// ==========================================================================

	// ==Distance
	// information====================================================
	protected double[][] s_EuclideanDistance; // euclidean distances of all pais
												// of instances所有实例的欧几里得距离
	protected double[][] s_HanminDistance; // hanmin distances of all pairs of
											// intances 所有实例的海明距离
	protected double[] s_targetValue; // Store the target concept value, for
										// speed up 存储目标概念值的上升速度
	protected double[] s_ebusilon; // Thereshold to generate adhere subsets 用来产生依附子集的阈值

	protected BitSet s_adHereFlag; // Store the flag show whether the instance
									// have nearest neighbors 存储实力是否有近邻
	protected BitSet[] s_bitMap; // Store the index that the corresponding
									// instance存储对应实例的索引
	// ==========================================================================
	protected double s_runtime;
	public static int s_numOfAttrs;
	public static HashMap<String, Integer> s_attrNames;
	// public static double s_runtime;

	// ==Complexity
	// metrics======================================================
	protected double s_BoundLength; // Length of class boundary
	protected double s_PerRetainedSubset; // Percentage of retained adherence
											// subsets
	protected double s_RatioNNDistances; // Ratio of average intra/inter class
											// nearest neighbor distances
	protected double s_nonLinearityLP; // Nonlinearity of linear classifier
	protected double s_nonLinearityNN; // Nonlinearity of NN
	protected double s_maximumFisher; // Maximum Fisher's discriminant ratio
	protected double s_SizeToDimen; // Number of instances/number of features

	// ==========================================================================

	public ComplexityBasedMetrics() {
	}
	public ComplexityBasedMetrics (Instances data) {
		s_dataInstances = data;
		s_numOfInstances = s_dataInstances.numInstances();
		s_numOfAttribute = s_dataInstances.numAttributes();
		int numOfTarget = s_dataInstances.numClasses();

		// **********************************************************************
		s_EuclideanDistance = new double[s_numOfInstances][]; // initialize the
																// Euclidean
																// distance
																// matrix
		s_HanminDistance = new double[s_numOfInstances][]; // initialize the
															// Hanmin distance
															// matrix
		s_targetValue = new double[s_numOfInstances];

		for (int i = 0; i < s_numOfInstances; i++) {
			s_EuclideanDistance[i] = new double[s_numOfInstances];
			s_HanminDistance[i] = new double[s_numOfInstances];
			for (int j = 0; j < s_numOfInstances; j++) {
				s_EuclideanDistance[i][j] = 0;
				s_HanminDistance[i][j] = 0;
			}
			s_targetValue[i] = 0;
		}
		// **********************************************************************
		s_ebusilon = new double[numOfTarget];
		s_classValueCount = new int[numOfTarget];
		for (int i = 0; i < s_ebusilon.length; i++) {
			s_ebusilon[i] = 0;
			s_classValueCount[i] = 0;
		}

		s_adHereFlag = new BitSet(s_numOfInstances);
		s_adHereFlag.clear();
		s_bitMap = new BitSet[s_numOfInstances];
		for (int i = 0; i < s_bitMap.length; i++) {
			s_bitMap[i] = new BitSet(s_numOfInstances);
			s_bitMap[i].clear();
		}

		s_runtime = 0;
		// **********************************************************************
		s_BoundLength = 0;
		s_PerRetainedSubset = 0;
		s_RatioNNDistances = 0;
		s_nonLinearityNN = 0;
		s_nonLinearityLP = 0;
		s_maximumFisher = 0;
		s_SizeToDimen = 0;
		// ======================================================================
		
	}

	public ComplexityBasedMetrics(String filename) throws IOException {
		Instances data = readArffData(filename);
		s_dataInstances = new Instances(data);
		s_numOfInstances = s_dataInstances.numInstances();
		s_numOfAttribute = s_dataInstances.numAttributes();
		int numOfTarget = s_dataInstances.numClasses();

		// **********************************************************************
		s_EuclideanDistance = new double[s_numOfInstances][]; // initialize the
																// Euclidean
																// distance
																// matrix
		s_HanminDistance = new double[s_numOfInstances][]; // initialize the
															// Hanmin distance
															// matrix
		s_targetValue = new double[s_numOfInstances];

		for (int i = 0; i < s_numOfInstances; i++) {
			s_EuclideanDistance[i] = new double[s_numOfInstances];
			s_HanminDistance[i] = new double[s_numOfInstances];
			for (int j = 0; j < s_numOfInstances; j++) {
				s_EuclideanDistance[i][j] = 0;
				s_HanminDistance[i][j] = 0;
			}
			s_targetValue[i] = 0;
		}
		
		// **********************************************************************
		s_ebusilon = new double[numOfTarget];
		s_classValueCount = new int[numOfTarget];
		for (int i = 0; i < s_ebusilon.length; i++) {
			s_ebusilon[i] = 0;
			s_classValueCount[i] = 0;
		}

		s_adHereFlag = new BitSet(s_numOfInstances);
		s_adHereFlag.clear();
		s_bitMap = new BitSet[s_numOfInstances];
		for (int i = 0; i < s_bitMap.length; i++) {
			s_bitMap[i] = new BitSet(s_numOfInstances);
			s_bitMap[i].clear();
		}

		s_runtime = 0;
		// **********************************************************************
		s_BoundLength = 0;
		s_PerRetainedSubset = 0;
		s_RatioNNDistances = 0;
		s_nonLinearityNN = 0;
		s_nonLinearityLP = 0;
		s_maximumFisher = 0;
		s_SizeToDimen = 0;
		// ======================================================================
	}

	public void computeDistance() {
		for (int i = 0; i < s_numOfInstances; i++) {
			Instance insi = s_dataInstances.instance(i);
			s_targetValue[i] = insi.classValue();
			int classIndex = (int) s_targetValue[i];
			s_classValueCount[classIndex]++;
			// **************************************************************
			for (int j = i + 1; j < s_numOfInstances; j++) {
				Instance insj = s_dataInstances.instance(j);
				s_HanminDistance[i][j] = hanDistance(insi, insj);
				s_HanminDistance[j][i] = s_HanminDistance[i][j];
				s_EuclideanDistance[i][j] = euDistance(insi, insj);
				s_EuclideanDistance[j][i] = s_EuclideanDistance[i][j];
			}
			// **************************************************************
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

	public double euDistance(Instance insi, Instance insj) {
		double distance = 0;
		for (int i = 0; i < s_numOfAttribute - 1; i++) {
			if (!insi.isMissing(i) && !insj.isMissing(i)) {
				distance = distance + (insi.value(i) - insj.value(i))
						* (insi.value(i) - insj.value(i));
			}
		}
		return fourDecimal(Math.sqrt(distance));
	}

	public double fourDecimal(double d) {
		return Math.floor(d * 10000 + 0.5) / 10000;
	}

	// ==The above codes are related to the preparation
	// =============================
	public void setClassBoundLength() {
		double[][] distanceMatrix = s_EuclideanDistance;

		rapidMSTree rmstree = new rapidMSTree(distanceMatrix, s_numOfInstances);
		rmstree.intialParameters();
		rmstree.RapidKruskalMST();
		// rmstree.showRapidMSTree();
		computeClassBoundRapid(rmstree.getEdgeIndexes());
		// System.out.println(this.s_BoundLength);
	}

	public void computeClassBoundRapid(int[] edgesInMSTree) {
		int[] tempIndex = edgesInMSTree;
		int edgeIndex, row, col;
		int numOfInstances = s_numOfInstances;
		Instance insRow, insCol;

		int numOfTargets = s_dataInstances.numClasses();

		if (numOfTargets == 2) {
			double classBound = 0;
			for (int i = 0; i < tempIndex.length; i++) {
				edgeIndex = tempIndex[i];
				row = edgeIndex / numOfInstances;
				col = edgeIndex % numOfInstances;

				insRow = s_dataInstances.instance(row);
				insCol = s_dataInstances.instance(col);
				// System.out.println(edgeIndex);

				if (insRow.classValue() != insCol.classValue()) {
					classBound++;
				}
			}
			s_BoundLength = fourDecimal(classBound / (s_numOfInstances - 1.0));
		} else {
			double[] classBounds = new double[numOfTargets];
			for (int i = 0; i < tempIndex.length; i++) {
				edgeIndex = tempIndex[i];
				row = edgeIndex / numOfInstances;
				col = edgeIndex % numOfInstances;

				insRow = s_dataInstances.instance(row);
				insCol = s_dataInstances.instance(col);
				// System.out.println(edgeIndex);

				int rowClassValue = (int) insRow.classValue();
				int colClassValue = (int) insCol.classValue();
				if (rowClassValue != colClassValue) {
					classBounds[rowClassValue]++;
					classBounds[colClassValue]++;
				}
			}

			double maxClassBound = 0;
			for (int i = 0; i < classBounds.length; i++) {
				if (classBounds[i] > maxClassBound) {
					maxClassBound = classBounds[i];
				}
				// System.out.println(classBounds[i]);
			}
			s_BoundLength = fourDecimal(maxClassBound
					/ (s_numOfInstances - 1.0));
		}
	}

	// ==========================================================================
	public void setPerRetainedSubset() {
		computeDistanceThreshold();
		int numOfTarget = s_dataInstances.numClasses();
		double[] retainCount = new double[numOfTarget];
		double maxRetainCount = Double.MIN_VALUE;
		for (int i = 0; i < numOfTarget; i++) {
			retainCount[i] = CountOfAdhereSets(i);
			retainCount[i] = fourDecimal(retainCount[i] / s_numOfInstances);
			if (maxRetainCount < retainCount[i]) {
				maxRetainCount = retainCount[i];
			}
		}
		s_PerRetainedSubset = maxRetainCount;
	}

	public void computeDistanceThreshold() {
		double[][] distances = s_EuclideanDistance;
		int numOfTarget = s_dataInstances.numClasses();

		for (int i = 0; i < numOfTarget; i++) {
			double targetValue = i;
			double delta = Double.MAX_VALUE;
			for (int j = 0; j < s_numOfInstances; j++) {
				for (int k = j + 1; k < s_numOfInstances; k++) {
					if (((s_targetValue[j] == targetValue) && (s_targetValue[k] != targetValue))
							|| ((s_targetValue[j] != targetValue) && (s_targetValue[k] == targetValue))) {
						if (delta > distances[j][k]) {
							delta = distances[j][k];
						}
					}
				}
			}
			s_ebusilon[i] = fourDecimal(0.55 * delta);
			// System.out.println(s_ebusilon[i]);
		}
	}

	public void SetAdhereFlag(int targetIndex) {
		double[][] distances = s_EuclideanDistance;
		// intial the BitSet varibles
		s_adHereFlag.clear();
		for (int i = 0; i < s_numOfInstances; i++) {
			s_bitMap[i].clear();
		}
		// ----------------------------------------------------------------------
		for (int i = 0; i < s_numOfInstances; i++) {
			for (int j = i + 1; j < s_numOfInstances; j++) {
				boolean stat1 = (s_targetValue[i] == s_targetValue[j]);
				boolean stat2 = ((s_targetValue[i] != targetIndex) && (s_targetValue[j] != targetIndex));
				if ((stat1 || stat2)
						&& (distances[i][j] < s_ebusilon[targetIndex])) {
					// System.out.println(i+"\t"+j);
					s_adHereFlag.set(i);
					s_adHereFlag.set(j);
					s_bitMap[i].set(j);
					s_bitMap[j].set(i);
				}
			}
		}
		// ----------------------------------------------------------------------
		// System.out.println(s_adHereFlag.cardinality());
	}

	public double CountOfAdhereSets(int targetIndex) {
		SetAdhereFlag(targetIndex);
		// System.out.println(s_ebusilon[targetIndex]);

		// System.out.println(s_adHereFlag.toString());
		BitSet[] tempBitMap = new BitSet[s_numOfInstances];
		BitSet tempFlag = new BitSet(s_numOfInstances);
		tempFlag.clear();
		for (int i = 0; i < s_bitMap.length; i++) {
			tempBitMap[i] = new BitSet();
			if (s_adHereFlag.get(i)) {
				BitSet tempSet = (BitSet) s_bitMap[i].clone();
				// System.out.print(tempSet.cardinality()+"=>");
				GenAdHereSet(tempSet);
				tempBitMap[i] = (BitSet) tempSet.clone();
				// System.out.println(tempSet.cardinality()+","+tempBitMap[i].cardinality());
			} else {
				tempFlag.set(i);
			}
		}

		// System.out.println(tempFlag.cardinality()+"\t"+s_adHereFlag.toString());

		for (int i = 0; i < s_numOfInstances; i++) {
			BitSet setClone = null;
			if (!tempFlag.get(i)) {
				// System.out.println(i+"\t"+tempBitMap[i].toString());
				for (int j = 0; j < s_numOfInstances; j++) {
					if (i != j && !tempFlag.get(j)) {
						setClone = (BitSet) tempBitMap[i].clone();
						if (setClone.cardinality() >= tempBitMap[j]
								.cardinality()) {
							// System.out.print(setClone.cardinality()+"=>");
							// System.out.println(i+"\t"+j+"\t"+setClone.toString()+"\t"+tempBitMap[j].toString());
							setClone.and(tempBitMap[j]);
							// System.out.println(setClone.cardinality());
							// System.out.println(setClone.equals(tempBitMap[j]));
							if (setClone.equals(tempBitMap[j])) {
								// System.out.println(i+"\t"+j+"\t"+tempFlag.cardinality());
								tempFlag.set(j);
							}
						}
					}
				}
			}
		}

		double count = s_numOfInstances - tempFlag.cardinality()
				+ s_numOfInstances - s_adHereFlag.cardinality();
		// System.out.println(tempFlag.cardinality()+"\t"+s_adHereFlag.cardinality());
		// System.out.println(count);
		return count;
	}

	public void GenAdHereSet(BitSet insi) {
		BitSet tempSet = null;
		do {
			tempSet = (BitSet) insi.clone();
			for (int i = tempSet.nextSetBit(0); i >= 0; i = insi
					.nextSetBit(i + 1)) {
				if (s_adHereFlag.get(i)) {
					insi.or(s_bitMap[i]);
				}
			}
			tempSet.andNot(insi);
		} while (!tempSet.isEmpty());
	}

	// ==========================================================================
	public void setNonLinearity() throws Exception {
		dataSetSplit();
		// genTestDataSet(0,100);

		int numOfTarget = s_dataInstances.numClasses();
		if (numOfTarget == 2) {
			numOfTarget = 1;
		}

		NaiveBayes LPclassifier = new NaiveBayes();

		IB1 NNclassifier = new IB1();
		int sizeOfTest = 10000;

		double maxErrorNN = Double.NEGATIVE_INFINITY;
		double maxErrorLP = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < numOfTarget; i++) {
			double errori = getClassificationError(NNclassifier, i, sizeOfTest);
			// System.out.println(errori);
			if (maxErrorNN < errori) {
				// System.out.println(errori);
				maxErrorNN = errori;
			}

			errori = getClassificationError(LPclassifier, i, sizeOfTest);
			// System.out.println(errori);
			if (maxErrorLP < errori) {
				maxErrorLP = errori;
			}
		}

		s_nonLinearityNN = maxErrorNN;
		s_nonLinearityLP = maxErrorLP;
	}

	public double getClassificationError(Classifier classifier, int whichClass,
			int sizeOfTest) throws Exception {
		Instances testInstances = genTestDataSet(whichClass, sizeOfTest);
		Instances trainInstances = new Instances(
				s_splitDataInstances[whichClass]);

		Classifier copiedClassifier = Classifier.makeCopy(classifier);
		copiedClassifier.buildClassifier(trainInstances);

		Evaluation evaluation = new Evaluation(testInstances);
		evaluation.evaluateModel(copiedClassifier, testInstances);

		double error = fourDecimal(evaluation.pctIncorrect() / 100);
		return error;
	}

	public Instances genTestDataSet(int whichClass, int sizeOfTest) {
		Instances testData = new Instances(s_splitDataInstances[whichClass], 0);
		Instances sourceData = new Instances(s_splitDataInstances[whichClass]);
		sourceData.sort(s_numOfAttribute - 1);
		// sourceData.randomize(null);

		int numOfThisClass = s_classValueCount[whichClass];

		Random random = new Random();

		for (int i = 0; i < sizeOfTest; i++) {
			int startIndex = random.nextInt(s_numOfInstances);
			Instance firstIns = sourceData.instance(startIndex);

			Instance secondIns = null;
			if (startIndex >= numOfThisClass) {
				int endIndex = random
						.nextInt(s_numOfInstances - numOfThisClass)
						+ numOfThisClass;
				secondIns = sourceData.instance(endIndex);
			} else {
				int endIndex = random.nextInt(numOfThisClass);
				secondIns = sourceData.instance(endIndex);
			}

			Instance combIns = combineInstance(firstIns, secondIns);
			testData.add(combIns);

			// System.out.println(firstIns.stringValue(s_numOfAttribute-1)+"\t"+secondIns.stringValue(s_numOfAttribute-1)+"\t"+combIns.stringValue(s_numOfAttribute-1));
		}

		return testData;
	}

	public Instance combineInstance(Instance firstIns, Instance secondIns) {
		Random random = new Random();
		double alpha = random.nextDouble();

		Instance combIns = new Instance(firstIns);
		combIns.setDataset(firstIns.dataset());
		for (int i = 0; i < s_numOfAttribute - 1; i++) {
			Attribute attri = combIns.attribute(i);

			if (attri.isNumeric()) {
				double attriValue = fourDecimal(alpha * firstIns.value(i)
						+ (1 - alpha) * secondIns.value(i));
				combIns.setValue(i, attriValue);
			} else {
				if (alpha < 0.5) {
					combIns.setValue(i, secondIns.stringValue(i));
				}
			}
		}

		// System.out.println(firstIns.toString()+"\n"+secondIns.toString()+"\n"+combIns.toString()+"\t"
		// + alpha);
		return combIns;
	}

	public void dataSetSplit() {
		int numOfTarget = s_dataInstances.numClasses();
		if (numOfTarget == 2) {
			s_splitDataInstances = new Instances[1];
			s_splitDataInstances[0] = new Instances(s_dataInstances);
		} else {
			s_splitDataInstances = genSubDataSets();
		}
	}

	public Instances[] genSubDataSets() {
		int numOfTarget = s_dataInstances.numClasses();

		Instances[] subDatasets = new Instances[numOfTarget];
		FastVector[] attrInforArray = new FastVector[numOfTarget];
		Attribute classAttribute = s_dataInstances.classAttribute();
		String[] classValues = new String[numOfTarget];

		String relationName = s_dataInstances.relationName();

		for (int i = 0; i < attrInforArray.length; i++) {
			String classAttrName = classAttribute.name();
			String classAttrValuei = classAttribute.value(i);
			classValues[i] = classAttrValuei;

			FastVector attrValues = new FastVector();
			attrValues.addElement(classAttrValuei);
			attrValues.addElement("Not_" + classAttrValuei);
			Attribute newClass = new Attribute(classAttrName, attrValues);

			attrInforArray[i] = new FastVector();
			for (int j = 0; j < s_numOfAttribute - 1; j++) {
				Attribute attrj = s_dataInstances.attribute(j);
				attrInforArray[i].addElement(attrj);
			}
			attrInforArray[i].addElement(newClass);

			String relationNamei = relationName + "" + i;
			subDatasets[i] = new Instances(relationNamei, attrInforArray[i],
					s_numOfInstances);
			subDatasets[i].setClassIndex(s_numOfAttribute - 1);
		}

		for (int i = 0; i < s_numOfInstances; i++) {
			Instance insi = s_dataInstances.instance(i);
			double[] attrValues = new double[s_numOfAttribute];
			for (int j = 0; j < s_numOfAttribute - 1; j++) {
				attrValues[j] = insi.value(j);
			}

			Instance newInsi = new Instance(1, attrValues);
			int classIndex = (int) insi.classValue();
			for (int k = 0; k < numOfTarget; k++) {
				if (k == classIndex) {
					Instance temp = new Instance(newInsi);
					temp.setDataset(subDatasets[k]);
					temp.setClassValue(classValues[k]);
					subDatasets[k].add(temp);
				} else {
					Instance temp = new Instance(newInsi);
					temp.setDataset(subDatasets[k]);
					temp.setClassValue("Not_" + classValues[k]);
					subDatasets[k].add(temp);
				}
			}
		}

		return subDatasets;
	}

	// ==========================================================================
	public void setRatioNNDistance() {
		double[][] distances = s_EuclideanDistance;
		double ratioNN = 0;
		double intraDisSum = 0;
		double interDisSum = 0;
		// ======================================================================
		for (int i = 0; i < s_numOfInstances; i++) {
			Instance insi = s_dataInstances.instance(i);
			double classValue = insi.classValue();
			double[] results = IdentifyNNInstances(classValue, i, distances[i]);
			intraDisSum = intraDisSum + results[0];
			interDisSum = interDisSum + results[1];
		}
		ratioNN = fourDecimal(intraDisSum / interDisSum);

		s_RatioNNDistances = ratioNN;
	}

	public double[] IdentifyNNInstances(double classValue, int insIndex,
			double[] distArray) {
		double[] results = new double[2];

		double minIntraDist = Double.MAX_VALUE;
		double minInterDist = Double.MAX_VALUE;

		for (int i = 0; i < s_numOfInstances; i++) {
			if (i != insIndex) {
				if (classValue == s_targetValue[i]) {
					if (distArray[i] < minIntraDist) {
						minIntraDist = distArray[i];
					}
				} else {
					if (distArray[i] < minInterDist) {
						minInterDist = distArray[i];
					}
				}
			}
		}

		results[0] = minIntraDist;
		results[1] = minInterDist;
		return results;
	}

	// ==========================================================================
	public void setRatioNNDistance1() {
		double[][] distances = s_EuclideanDistance;
		int numOfTarget = s_dataInstances.numClasses();
		double[] ratioNN = new double[numOfTarget];
		double[] intraDisSum = new double[numOfTarget];
		double[] interDisSum = new double[numOfTarget];

		for (int i = 0; i < s_numOfInstances; i++) {
			Instance insi = s_dataInstances.instance(i);
			int classValue = (int) insi.classValue();

			double[][] results = IdnetifyNNInstances(classValue, numOfTarget,
					i, distances[i]);
			for (int j = 0; j < results.length; j++) {
				intraDisSum[j] = intraDisSum[j] + results[j][0];
				interDisSum[j] = interDisSum[j] + results[j][1];
				// System.out.print(i+"\t"+results[j][0]+"\t"+results[j][1]);
			}
			// System.out.println();
		}

		double maxRatioNN = Double.MIN_VALUE;
		for (int i = 0; i < ratioNN.length; i++) {
			ratioNN[i] = fourDecimal(intraDisSum[i] / interDisSum[i]);
			// System.out.println(intraDisSum[i]+"\t"+interDisSum[i]+"\t"+ratioNN[i]);
			if ((maxRatioNN < ratioNN[i]) && (s_classValueCount[i] > 1)) {
				maxRatioNN = ratioNN[i];
			}
		}
		s_RatioNNDistances = maxRatioNN;
	}

	public double[][] IdnetifyNNInstances(int classValue, int numOfTarget,
			int insIndex, double[] distArray) {
		double[][] results = new double[numOfTarget][2];
		for (int i = 0; i < numOfTarget; i++) {
			results[i] = new double[2];
			for (int j = 0; j < results[i].length; j++) {
				results[i][j] = 0;
			}
		}
		// ======================================================================
		for (int i = 0; i < numOfTarget; i++) {
			int targetValue = i;
			double minIntraDistance = Double.MAX_VALUE;
			double minInterDistance = Double.MAX_VALUE;

			for (int j = 0; j < s_numOfInstances; j++) {
				if (j != insIndex) {
					if (targetValue == classValue) {
						if (targetValue == s_targetValue[j]) {
							if (distArray[j] < minIntraDistance) {
								minIntraDistance = distArray[j];
							}
						} else {
							if (distArray[j] < minInterDistance) {
								minInterDistance = distArray[j];
							}
						}
					} else {
						if (targetValue == s_targetValue[j]) {
							if (distArray[j] < minInterDistance) {
								minInterDistance = distArray[j];
							}
						} else {
							if (distArray[j] < minIntraDistance) {
								minIntraDistance = distArray[j];
							}
						}
					}
				}
			}

			results[targetValue][0] = minIntraDistance;
			results[targetValue][1] = minInterDistance;
		}

		return results;
	}

	// ==========================================================================
	public void setMaxFisher() {
		FastVector numericIndexes = new FastVector();
		int numericCount = 0;
		for (int i = 0; i < s_numOfAttribute; i++) {
			Attribute attri = s_dataInstances.attribute(i);
			if (attri.isNumeric()) {
				numericCount++;
				numericIndexes.addElement(i);
			}
		}

		// System.out.println(numericCount);
		if (numericCount == 0) {
			s_maximumFisher = 0;
			return;
		}

		// double[] maxFishers = new double[numericCount];

		int numOfTarget = s_dataInstances.numClasses();

		double[] freqTarget = new double[numOfTarget];
		for (int i = 0; i < numOfTarget; i++) {
			freqTarget[i] = 0;
		}

		double[][] meanValueMatrix = new double[numericCount][];
		double[][] meanValue2Matrix = new double[numericCount][];

		for (int i = 0; i < numericCount; i++) {
			meanValueMatrix[i] = new double[numOfTarget];
			meanValue2Matrix[i] = new double[numOfTarget];
			for (int j = 0; j < numOfTarget; j++) {
				meanValueMatrix[i][j] = 0;
				meanValue2Matrix[i][j] = 0;
			}
		}
		// ======================================================================
		for (int i = 0; i < s_numOfInstances; i++) {
			Instance insi = s_dataInstances.instance(i);
			int classIndex = (int) insi.classValue();
			freqTarget[classIndex]++;

			for (int j = 0; j < numericCount; j++) {
				int numericIndex = Integer.parseInt(numericIndexes.elementAt(j)
						.toString());
				double x = insi.value(numericIndex);
				double x2 = x * x;

				// System.out.println(numericIndex+"\t"+classIndex+"\t"+meanValueMatrix.length);
				meanValueMatrix[j][classIndex] = meanValueMatrix[j][classIndex]
						+ x;
				meanValue2Matrix[j][classIndex] = meanValue2Matrix[j][classIndex]
						+ x2;
			}
		}
		// ======================================================================
		double[][] fisherMatrix = new double[numericCount][];

		for (int i = 0; i < numericCount; i++) {
			fisherMatrix[i] = new double[numOfTarget];
			for (int j = 0; j < numOfTarget; j++) {
				fisherMatrix[i][j] = 0;
			}
		}

		double[] sumX = new double[numericCount];
		double[] sumX2 = new double[numericCount];

		for (int i = 0; i < numericCount; i++) {
			sumX[i] = 0;
			sumX2[i] = 0;
			for (int j = 0; j < numOfTarget; j++) {
				sumX[i] = sumX[i] + meanValueMatrix[i][j];
				sumX2[i] = sumX2[i] + meanValue2Matrix[i][j];
			}
		}
		// ======================================================================
		for (int i = 0; i < numericCount; i++) {
			double[] tempx = meanValueMatrix[i];
			double[] tempx2 = meanValue2Matrix[i];

			for (int j = 0; j < numOfTarget; j++) {
				double miu1 = fourDecimal(tempx[j] / freqTarget[j]);
				double miu2 = fourDecimal((sumX[i] - tempx[j])
						/ (s_numOfInstances - freqTarget[j]));

				double var1 = fourDecimal(tempx2[j] / freqTarget[j]) - miu1
						* miu1;
				double var2 = fourDecimal((sumX2[i] - tempx2[j])
						/ (s_numOfInstances - freqTarget[j]))
						- miu2 * miu2;

				fisherMatrix[i][j] = fourDecimal((miu1 - miu2) * (miu1 - miu2)
						/ (var1 + var2));
			}
		}
		// ======================================================================
		double maxFisher = 0;
		for (int i = 0; i < fisherMatrix.length; i++) {
			for (int j = 0; j < fisherMatrix[i].length; j++) {
				// System.out.println(fisherMatrix[i][j]);
				if (fisherMatrix[i][j] > maxFisher) {
					maxFisher = fourDecimal(fisherMatrix[i][j]);
				}
			}
			// System.out.println();
		}
		s_maximumFisher = maxFisher;
		// ======================================================================
	}

	// ==========================================================================
	public void setInsToDimen() {
		s_SizeToDimen = fourDecimal(((double) s_numOfInstances)
				/ (s_numOfAttribute - 1.0));
	}

	// ==========================================================================

	public void computeComplexityMetrics() throws Exception {
		long startTime = System.nanoTime();
		computeDistance();
		setClassBoundLength();// 1)
		setMaxFisher();// 5)
		setInsToDimen();// 6)
		setRatioNNDistance1();// 3)
		setPerRetainedSubset();// 2)
		setNonLinearity();// 4)
		long endTime = System.nanoTime();
		s_runtime = fourDecimal((endTime - startTime) / 1000000.0);
	}

	public String outputMetrics() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.s_BoundLength + ",");
		sb.append(this.s_PerRetainedSubset + ",");
		sb.append(this.s_RatioNNDistances + ",");
		sb.append(this.s_nonLinearityLP + "," + this.s_nonLinearityNN + ",");
		sb.append(this.s_maximumFisher + ",");
		sb.append(this.s_SizeToDimen);
//		sb.append(s_runtime);

		return sb.toString();
	}

	public double[] achieveMetrics() {
		double[] metrics = new double[7];
		metrics[0] = s_BoundLength;
		metrics[1] = s_PerRetainedSubset;
		metrics[2] = s_RatioNNDistances;
		metrics[3] = s_nonLinearityLP;
		metrics[4] = s_nonLinearityNN;
		metrics[5] = s_maximumFisher;
		metrics[6] = s_SizeToDimen;

		return metrics;
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

	// public void performComplex (String filename,ComplexityBasedMetrics cmb)
	// throws Exception{
	//
	// Instances data = readArffData(filename);
	//
	// long startTime = System.nanoTime();
	// new ComplexityBasedMetrics(data);
	// computeComplexityMetrics();
	// long endTime = System.nanoTime();
	// double runtime = (endTime - startTime)/1000000.0;
	// String line = outputMetrics();
	// System.out.println(filename+","+line);
	// }
	public static void main(String[] args) throws IOException, Exception {
		
//		FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\result\\dataCharacteristic\\oidsData\\complexityBasedMetrics.csv",true);
//		PrintStream ps = new PrintStream(out);
//		System.setOut(ps);
		
		String filePath = "E:\\experiment\\dataset\\UCI\\dataSetOids\\test\\";
		fileHandle fh = new fileHandle();
		String[] fileNames = fh.getFileNames(filePath, "arff");
		for (int i = 0; i < fileNames.length; i++) {
			// System.out.println(fileNames[i]);

			String fileName = fileNames[i];
			// ComplexityBasedMetrics mycomplex = new ComplexityBasedMetrics();
			// mycomplex.performComplex(filePath+fileName,mycomplex);

			//long startTime = System.nanoTime();
			ComplexityBasedMetrics cbm = new ComplexityBasedMetrics(filePath + fileName);
			cbm.computeComplexityMetrics();
		//	long endTime = System.nanoTime();
		//	double runtime = (endTime - startTime) / 1000000.0;
			String line = cbm.outputMetrics();
			System.out.println((i + 1) + "," + fileName + "," + line);
			// System.out.println(fileNames+","+runtime);
		}

	}
}
