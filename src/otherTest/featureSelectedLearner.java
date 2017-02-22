/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package otherTest;

import fileUtil.fileHandle;
import java.io.File;
import java.io.IOException;
import learnAlgRecommendation.MyCrossValidation;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IB1;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ArffLoader;

/**
 *
 * @author String
 */
public class featureSelectedLearner {

    public static int numOfNomAttrs = 0;

    public static void main(String []args) throws IOException, Exception{
//        String filePath = "E:\\My works\\Experiment results\\" +
//                "Algorithm Recommendation\\Novel data set characteristics" +
//                "\\metrics\\traditional\\";
//
//        String fileNames[] = {"BC2.arff","BC3.arff","BC4.arff","BC5.arff","BC6.arff",
//        "GC2.arff","GC3.arff","GC4.arff","GC5.arff","GC6.arff"};
//
//        for(int i = 0; i < fileNames.length; i++){
//            System.out.println(fileNames[i]);
//            Instances dataset = readData(filePath + fileNames[i]);
//            learnOnDataset(dataset);
//        }
        String filePath = "E:\\My works\\Experiment results\\" +
                "Frameworks of Algorithm Recommendation\\UCI Data Sets\\Classification\\";

        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(filePath, "arff",true);

        for(int i = 0; i < fileNames.length; i++){
            readData(filePath + fileNames[i] + ".arff");
        }

        System.out.println(numOfNomAttrs);
    }

    public static Instances readData(String fileName) throws IOException {
        ArffLoader arffLoader = new ArffLoader();
        File file = new File(fileName);
        arffLoader.setFile(file);

        Instances dataset = arffLoader.getDataSet();
        int classIndex = dataset.numAttributes() - 1;
        dataset.setClassIndex(classIndex);

        for(int i = 0; i < dataset.numAttributes(); i++){
            if(dataset.attribute(i).isNominal()){
                numOfNomAttrs++;
            }
        }

        return dataset;
    }

    public static StringBuffer learnOnDataset(Instances dataset) throws Exception{

        AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();

//        J48 subclassifier = new J48();
//        NaiveBayes subclassifier = new NaiveBayes();
        IB1 subclassifier = new IB1();

        WrapperSubsetEval selector = new WrapperSubsetEval();

        BestFirst searcher = new BestFirst();
        selector.setClassifier(subclassifier);

//        SelectedTag st = new SelectedTag(1, BestFirst.TAGS_SELECTION);
//        searcher.setDirection(st);

        classifier.setClassifier(subclassifier);
        classifier.setEvaluator(selector);
        classifier.setSearch(searcher);

//        NaiveBayes classifier = new NaiveBayes();

        MyCrossValidation MCV = new MyCrossValidation(dataset);
        MCV.setClassifier(classifier);
        MCV.MXNCrossValidation(5, 10);

        double[] accs = MCV.getMetricsSole();

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < accs.length; i++){
            sb.append(accs[i]+"\n");
        }

        System.out.println(sb.toString());
        return sb;
    }
}
