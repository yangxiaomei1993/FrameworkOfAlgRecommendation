/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package learnAlgRecommendation;

import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

/**
 *
 * @author String
 */
public class MyCrossValidation extends Evaluation {

    protected double[][] s_runtimes;
    protected double[][] s_performanceMetrics;
    protected Instances s_dataset;
    protected Classifier s_classifier;
    
    

    public MyCrossValidation(Instances data) throws Exception{
        super(data);
        s_dataset = new Instances(data);
    }

    public void setClassifier(Classifier classifier){
        s_classifier = classifier;
    }

    public void MXNCrossValidation(int passNum, int foldsNum) throws Exception{
        s_performanceMetrics = new double[passNum][];
        s_runtimes = new double[passNum][];

        for(int i = 0; i < passNum; i++){
            s_performanceMetrics[i] = new double[foldsNum];
            s_runtimes[i] = new double[foldsNum];
            for(int j = 0; j < foldsNum; j++){
                s_performanceMetrics[i][j] = 0;
                s_runtimes[i][j] = 0;
            }
        }

        for(int randSeed = 1; randSeed <= passNum; randSeed++){             
             crossValidation(randSeed-1, foldsNum, randSeed);
        }
    }
    
  public void crossValidation(int passIndex, int foldsNum, int randSeed) throws Exception {
        Instances data = new Instances(s_dataset);
        Random random = new Random(randSeed);
        data.randomize(random);
        if (data.classAttribute().isNominal()) {
            data.stratify(foldsNum);
        }
//==============================================================================
        double prevCorrectCount = this.correct();
        double prevInCorrectCount = this.incorrect();
        double currentCorretCount = 0;
        double currentIncorretCount = 0;
        double accFoldi = 0;
        double runtimeFoldi = 0;
//==============================================================================
        // Do the folds
        for (int i = 0; i < foldsNum; i++) {
            long startTime = System.nanoTime();
            Instances train = data.trainCV(foldsNum, i, random);
            setPriors(train);
            Classifier copiedClassifier = Classifier.makeCopy(s_classifier);
            copiedClassifier.buildClassifier(train);
            Instances test = data.testCV(foldsNum, i);
            evaluateModel(copiedClassifier, test);
            long runtime = System.nanoTime() - startTime;
            currentCorretCount = this.correct() - prevCorrectCount;
            currentIncorretCount = this.incorrect() - prevInCorrectCount;
            prevCorrectCount = this.correct();
            prevInCorrectCount = this.incorrect();
            accFoldi = fourDecimal(currentCorretCount/(currentCorretCount+currentIncorretCount));
            runtimeFoldi = fourDecimal(((double) runtime)/1000000);
            s_performanceMetrics[passIndex][i] = accFoldi;
            s_runtimes[passIndex][i] = runtimeFoldi;
            
        }
        m_NumFolds = foldsNum;
        
    }    

    public void crossValidation(int foldsNum, int randSeed, Instances data) throws Exception {
        Random random = new Random(randSeed);
        data.randomize(random);
        if (data.classAttribute().isNominal()) {
            data.stratify(foldsNum);
        }
        // Do the folds
        for (int i = 0; i < foldsNum; i++) {
            long startTime = System.nanoTime();
            Instances train = data.trainCV(foldsNum, i, random);
            setPriors(train);
            Classifier copiedClassifier = Classifier.makeCopy(s_classifier);
            copiedClassifier.buildClassifier(train);
            Instances test = data.testCV(foldsNum, i);
            evaluateModel(copiedClassifier, test);
            long runtime = System.nanoTime() - startTime;            
        }
        
    }

    public double[][] getRuntimes(){
        return this.s_runtimes;
    }

    public double[][] getPerformanceMetrics(){
        return this.s_performanceMetrics;
    }

    public double[] getRuntimesSole(){
        int pass = s_runtimes.length;
        int folds = s_runtimes[0].length;

        int len = pass*folds;
        double times[] = new double[len];

        int index = 0;
        for(int i = 0; i < pass; i++){
            for(int j = 0; j < folds; j++){
                times[index] = s_runtimes[i][j];
                index++;
            }
        }

        return times;
    }

    public double[] getMetricsSole() {
        int pass = s_performanceMetrics.length;
        int folds = s_performanceMetrics[0].length;

        int len = pass * folds;
        double metrics[] = new double[len];

        int index = 0;
        for (int i = 0; i < pass; i++) {
            for (int j = 0; j < folds; j++) {
                metrics[index] = s_performanceMetrics[i][j];
                index++;
            }
        }

        return metrics;
    }

    public static double fourDecimal(double d) {
        return Math.floor(d * 10000 + 0.5) / 10000;
    }
}
