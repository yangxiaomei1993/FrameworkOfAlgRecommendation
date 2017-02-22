/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataCharacteristics;

import java.util.Random;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * 
 * @author String
 */
public class SimpleDecisionTree extends Classifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8418165074011512450L;
	public static final int s_BEST = 0;
	public static final int s_WORST = 1;
	public static final int s_RAND = 2;

	protected int s_TreeNodeID;
	protected double[] s_ClassValues;
	protected int s_Mode;

	protected void setMode(int mode) {
		s_Mode = mode;
	}

	@Override
	public void buildClassifier(Instances train) throws Exception {
		Instances data = new Instances(train, 0, train.numInstances());
		data.deleteWithMissingClass();

		GainRatioAttributeEval attrEval = new GainRatioAttributeEval();
		attrEval.buildEvaluator(data);

		int numOfAttributes = data.numAttributes() - 1;
		double[] gainRatios = new double[numOfAttributes];
		for (int i = 0; i < gainRatios.length; i++) {
			gainRatios[i] = attrEval.evaluateAttribute(i);
			// System.out.println(gainRatios[i]);
		}

		switch (s_Mode) {
		case s_BEST: {
			double maxGain = Double.MIN_VALUE;
			for (int i = 0; i < gainRatios.length; i++) {
				if (maxGain < gainRatios[i]) {
					maxGain = gainRatios[i];
					s_TreeNodeID = i;
				}
			}
		}
			break;

		case s_WORST: {
			double minGain = Double.MAX_VALUE;
			for (int i = 0; i < gainRatios.length; i++) {
				if (minGain > gainRatios[i]) {
					minGain = gainRatios[i];
					s_TreeNodeID = i;
				}
			}
		}
			break;
		case s_RAND: {
			Random rand = new Random();
			rand.setSeed(1);
			s_TreeNodeID = rand.nextInt(numOfAttributes);
		}
			break;
		default:
			System.err.println("Error mode");
		}

		int numOfTargets = data.numClasses();
		int numOfNodeSplits = data.attribute(s_TreeNodeID).numValues();

		double[][] classDistribution = new double[numOfNodeSplits][];
		for (int i = 0; i < classDistribution.length; i++) {
			classDistribution[i] = new double[numOfTargets];
			for (int j = 0; j < classDistribution[i].length; j++) {
				classDistribution[i][j] = 0;
			}
		}

		for (int i = 0; i < data.numInstances(); i++) {
			Instance insi = data.instance(i);
			if ((!insi.classIsMissing()) && (!insi.isMissing(s_TreeNodeID))) {
				int attrValue = (int) insi.value(s_TreeNodeID);
				int classValue = (int) insi.classValue();
				classDistribution[attrValue][classValue]++;
			}
		}

		s_ClassValues = new double[numOfNodeSplits];
		for (int i = 0; i < classDistribution.length; i++) {
			double[] array = classDistribution[i];
			double temp = Double.MIN_VALUE;
			for (int j = 0; j < array.length; j++) {
				if (temp < array[j]) {
					temp = array[j];
					s_ClassValues[i] = j;
				}
			}
		}
	}

	@Override
	public double classifyInstance(Instance instance) {
		int modeValue = (int) instance.value(s_TreeNodeID);

		return s_ClassValues[modeValue];
	}
}
