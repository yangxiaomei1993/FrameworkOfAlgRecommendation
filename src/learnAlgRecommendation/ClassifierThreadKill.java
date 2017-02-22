package learnAlgRecommendation;

import weka.classifiers.Classifier;
import weka.core.Instances;

/** 
* @author  Yang Xiaomei 
* @date 2016年8月30日 下午4:30:43 
* @description  
*/
public class ClassifierThreadKill implements Runnable{
	
	private MyCrossValidation MCV;
	private Instances dataset;
	private Classifier classifier;
	private int passNum;
	private int foldNum;
	
	private boolean isFinished=false;
	

	public ClassifierThreadKill() {
		// TODO Auto-generated constructor stub
	}
	



	public ClassifierThreadKill(MyCrossValidation mCV, Instances dataset, Classifier classifier, int passNum,
			int foldNum) {
		super();
		MCV = mCV;
		this.dataset = dataset;
		this.classifier = classifier;
		this.passNum = passNum;
		this.foldNum = foldNum;
	}

	@Override
	public void run() {
		MCV.setClassifier(classifier);
        try {
			MCV.MXNCrossValidation(passNum, foldNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        isFinished=true;
		// TODO Auto-generated method stub
		
	}
	
	public double[] getAcc() {
		return MCV.getMetricsSole();
		
		
	}
	public double[] getTimes() {
		return MCV.getRuntimesSole();
		
	}




	/**
	 * @return the isFinished
	 */
	public boolean isFinished() {
		return isFinished;
	}




	/**
	 * @param isFinished the isFinished to set
	 */
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	

}
