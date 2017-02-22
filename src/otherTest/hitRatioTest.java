/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package otherTest;

import fileUtil.fileOperator;

/**
 *
 * @author String
 */
public class hitRatioTest {

    public static void main(String[] args){
        String fileName = "E:\\My works\\Experiment results\\Pattern Regnization (Chao)\\Best algorithm set\\best_10.txt";
        fileOperator fo = new fileOperator();

        boolean[][] flags = new boolean[84][];
        for(int i = 0; i < flags.length; i++){
            flags[i] = new boolean[17];
            for(int j = 0; j < flags[i].length; j++){
                flags[i][j] = false;
            }
        }
        

        fo.openReadFile(fileName);
        String line = fo.readByLine();
        int lineCount = 1;
        while(line != null){
            String[] tokens = line.split("	");
            for(int i = 0; i < tokens.length; i++){
                int index = Integer.parseInt(tokens[i].trim());
                flags[lineCount-1][index-1] = true;
            }
//            System.out.println(tokens.length);
            line = fo.readByLine();
            lineCount++;
        }
        fo.closeReadFile();

        for(int i = 0; i < flags.length; i++){
            for(int j = 0; j < flags[i].length; j++){
                if(flags[i][j]){
                    System.out.print(1+",");
                }else{
                    System.out.print(0+",");
                }
            }
            System.out.println();
        }
    }
}
