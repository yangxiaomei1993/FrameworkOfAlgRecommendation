/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package otherTest;

import fileUtil.fileHandle;

/**
 *
 * @author String
 */
public class matlabFile {

    public static void main(String []args){
//        String filePath = "E:\\My works\\Experiment results\\" +
//                "Algorithm Recommendation\\Classification Results\\accuracy\\";

//        String filePath = "E:\\My works\\Experiment results\\Frameworks of Algorithm Recommendation\\accuracy\\";
//
//        fileHandle fh = new fileHandle();
//        String fileNames[] = fh.getFileNames(filePath, "txt");
//
//        for(int i = 0; i < fileNames.length; i++){
//            System.out.println((i+1)+"\t"+fileNames[i]);
//        }


//        int[] runFirstMethIndexes = {0,1,5,6,7,8,9,10,11};
//
//        for(int i = 0; i < runFirstMethIndexes.length; i++){
//           System.out.println(learnAlgRecommendation.runDifferentClassifiers.methodNames[runFirstMethIndexes[i]]);
//        }
//        String comandLine = "data = importdata(strcat(filePath,";
//        StringBuffer sb = new StringBuffer();
//
//        for(int i = 0; i < fileNames.length; i++){
//            sb.append(comandLine + "\'"+fileNames[i]+"\'));\naccData(:,:,"+(i+1)+")=data;\n");
//        }
//
//        System.out.println(sb.toString());
        
        String dataFilePath = "E:\\Phd paper experimental results\\Coding data sets\\";

        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(dataFilePath, "txt", false);
        
        StringBuilder sb = new StringBuilder();
        sb.append("metrics = zeros(115,31);\n");
        
        for(int i = 0; i < fileNames.length; i++){
            sb.append("data = importdata(\'"+  dataFilePath + fileNames[i]+ "\');\n");
            sb.append("tradMetrics = tradMetricCollection(data);\n");
            sb.append("metrics("+ (i+1) + ", :) = tradMetrics;\n");
        }
        
        System.out.println(sb.toString());
    }
}
