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
public class batGnerate {

    public static void main(String[] args){
//        String filePath = "E:\\My works\\Experiment results\\Algorithm Recommendation\\data set (Chao)\\";
//        String filePath = "E:\\My works\\Experiment results\\" +
//                "Frameworks of Algorithm Recommendation\\UCI Data Sets\\" +
//                "All Data sets\\";
        
        String filePath = "E:\\My works\\Experiment results\\"
                + "Classification Algorithm Recommendation by Clustering\\Data set\\";
        
        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(filePath, "arff");
        
        System.out.println(java.util.Arrays.toString(fileNames));

        String command = "java -Xmx1024m -cp FrameworkOfAlgRecommendation.jar dataCharacteristics.LandMarkMetrics ";
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < fileNames.length; i++){
            sb.append("title "+ (i+1) +"_"+fileNames[i]+"\n");
            sb.append(command + fileNames[i] +" >>results.txt\n\n");
        }

        System.out.println(sb.toString());
    }
}
