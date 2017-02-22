/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package matlabFilePackage;

import fileUtil.fileHandle;

/**
 *
 * @author String
 */
public class readFile {

//    public static void main(String []args){
//        String filePath = "E:\\My works\\Experiment results\\Algorithm Recommendation" +
//                "\\Novel data set characteristics\\accuracy\\";
//
//        fileHandle fh = new fileHandle();
//        String fileNames[] = fh.getFileNames(filePath, "txt");
//
//        StringBuffer sb = new StringBuffer();
//        sb.append("accArray = zeros(9,50,74);\n");
//        for(int i = 0; i < fileNames.length; i++){
//            sb.append("data = importdata(\'"+filePath+fileNames[i]+"\');\n");
//            sb.append("accArray(:,:,"+(i+1)+") = data;\n");
//        }
//
//        System.out.println(sb.toString());
//    }
//    public static final String fileNamePath = "E:\\My works\\Experiment results\\"
//            + "Frameworks of Algorithm Recommendation\\"
//            + "UCI Data Sets\\Classification\\";

//    public static final String dataFilePath = "E:\\My works\\Experiment results\\" +
//            "Frameworks of Algorithm Recommendation\\Meta-targets\\AAR Information (0.1%)\\";

    public static final String txtFilePath = "E:\\My works\\Experiment results\\" +
            "Frameworks of Algorithm Recommendation\\UCI Data Sets\\txtForm\\";

    public static final String dataFilePath = "E:\\My works\\Experiment results\\" +
            "Frameworks of Algorithm Recommendation\\Meta-targets\\AAR Information (10%)\\";
    
    public static final String fileNamePath = "E:\\My works\\Experiment results\\"
            + "Frameworks of Algorithm Recommendation\\UCI Data Sets\\All Data sets\\";

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        fileHandle fh = new fileHandle();
        String fileNames[] = fh.getFileNames(fileNamePath, "arff", true);

//        sb.append("metrics = zeros(1090,31);\n");
//        sb.append("runtime = zeros(1090,1);\n");
//        for(int i = 0; i < fileNames.length; i++){
//            sb.append("data = importdata(\'"+ txtFilePath + fileNames[i]+".txt\');\n");
//            sb.append("starttime=cputime;\n");
//            sb.append("tradMetrics = tradMetricCollection(data);\n");
//            sb.append("extime= starttime - cputime;\n");
//            sb.append("metrics("+(i+1)+",:) = tradMetrics;\n");
//            sb.append("runtime("+(i+1)+") = extime;\n");
//        }
//
//        System.out.println(sb.toString());

//        StringBuffer sb = new StringBuffer();
//        fileHandle fh = new fileHandle();
//        String fileNames[] = fh.getFileNames(fileNamePath, "arff", true);
//
//        System.out.println(fileNames.length);
//
//        String commandLine = "java -Xmx512m -cp FrameworkOfAlgRecommendation.jar " +
//                "dataCharacteristics.ModelBasedMetrics ";
//
//
//        for(int i = 0; i < fileNames.length; i++){
//            sb.append("title "+fileNames[i]+(i+1)+"\n");
//            sb.append(commandLine + fileNames[i] + ".arff >> results.txt\n");
//        }
//
//        System.out.println(sb.toString());

//        for(int i = 0; i < fileNames.length; i++){
//            System.out.println((i+1)+"\t"+fileNames[i].replace('.', '_'));
//        }

//        sb.append("optAlgArray = zeros(1090,2);\n");
//        for (int i = 0; i < fileNames.length; i++) {
//            sb.append("data = importdata(\'"+dataFilePath+fileNames[i].replace('.', '_')+".mat\');\n");
//            sb.append("[OptAlg, OptValue] = IdentifyOptimalAlgorithm(data);\n");
//            sb.append("optAlgArray("+(i+1)+",:) = [OptAlg, OptValue];\n");
////            System.out.println("ComputeAARonFile(\'" + fileNames[i] + "\')");
//        }

//        sb.append("clc;\nclear all;\n");
//        sb.append("meanPerformance = zeros(1090,13);\n");
//        for (int i = 0; i < fileNames.length; i++) {
//            sb.append("data = importdata(\'" + dataFilePath + fileNames[i] + ".txt\');\n");
//            sb.append("meanPerformance(" + (i + 1) + ",:) = mean(data);\n");
////            System.out.println("ComputeAARonFile(\'" + fileNames[i] + "\')");
//        }

        sb.append("clc;\nclear all;\n");
        sb.append("meanPerformance = zeros(1090,13);\n");
        for (int i = 0; i < fileNames.length; i++) {
            sb.append("data = importdata(\'" + dataFilePath + fileNames[i].replace('.', '_') + ".mat\');\n");
            sb.append("[RankMatrix tieFlag] = calculateRank(data);\n");
            sb.append("meanPerformance(" + (i + 1) + ",:) = 14 - mean(RankMatrix);\n");
//            System.out.println("ComputeAARonFile(\'" + fileNames[i] + "\')");
        }

//        sb.append("clc;\nclear all;\n");
//        sb.append("optAlgMatrix = zeros(1090,13);\n");
//        sb.append("alphaLevel = 0.05;\n");
//        for(int i = 0; i < fileNames.length; i++){
//            sb.append("data = importdata(\'" + dataFilePath + fileNames[i].replace('.', '_')+".mat\');\n");
//            sb.append("[appIndexes TArray pArray] = FriedManbyHolmProcedure(data, alphaLevel);\n");
//            sb.append("optAlgMatrix("+(i+1) + ",:) = appIndexes;\n");
//        }

//        sb.append("clc;\nClear all;\n");
//        for(int i = 0; i < fileNames.length; i++){
//            sb.append("ComputeAARonFile(\'"+fileNames[i]+"\');\n");
//        }
//
        System.out.println(sb.toString());
        
    }
}
