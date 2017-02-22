package metaTarget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import javax.naming.InitialContext;

import dataCharacteristics.newMetrics;
import fileUtil.fileHandle;
import fileUtil.fileOperator;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/** 
* @author  Yang Xiaomei 
* @date 201numOfResults��9��20�� ����2:05:00 
* @description  
*/
public class TimePreprocess {
	
	private static int initialTime = 3600000;//һ��Сʱ
	
    public static final int numOfAlgs = 21;
    public static final int numOfResults = 50;
    

	public static String[] getDataFileNames(String fileNamePath){
        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(fileNamePath, "txt", true);
        return fileNames;
    }
	

    public static double[][] readDataArrayInfor(String fileNamePath, String dataName) throws IOException{
        
    	FileReader fr = new FileReader(fileNamePath + dataName + ".txt");
    	BufferedReader br = new BufferedReader(fr);
        double[][] data = new double[numOfAlgs][numOfResults];
        
        for(int i = 0; i < numOfAlgs; i++){
            data[i] = new double[numOfResults];
            for(int j = 0; j < data[i].length; j++){
                data[i][j] = 0;
            }
        }

        for(int i = 0; i < numOfAlgs; i++){
	    		double[] dataLine = new double[numOfResults];
				String line = br.readLine();

				String[] tokens = line.split(",");

				for (int j=0;j<numOfResults;j++)
				{
					dataLine[j] = Double.parseDouble(tokens[j].toString().trim());
							if (line!=""){
								data[i][j] = dataLine[j];					                
											}
			

				}
       }
        
        return data;
    }
  
   

    public static boolean isZero(double[]data) {
    	boolean iszero = false;
    	for (int i = 0;i < numOfResults;i++)
    	{
    		if (data[i]==0)
    			iszero = true;
    				}
    	return iszero;
	}//�����0���ڵĻ�������true
    
    public static double[][] modify(double[][] data ) {
    	for (int j = 0;j < numOfAlgs;j++)
    	{
    		if ( isZero(data[j]) )
    		{
    			for (int i = 0;i < numOfResults; i++)
    			{
    				data[j][i] = initialTime;
    			}
    		}
    		
    	}
		return data;
	}

    public static void saveResults(String dataName, double[][] data){
        fileOperator fo = new fileOperator();
        fo.openWriteFile(dataName);
        for(int i = 0; i < numOfAlgs; i++){
            String line = "" + data[i][0];
            for(int j = 1; j < numOfResults; j++){
                line = line + "," + data[i][j];
            }
            fo.writeFile(line);
        }

        fo.closeWriteFile();
    }

    public static void main(String[] args) throws IOException{
    	String openPath="E:\\experiment\\result\\Classification Accuracy and Runtime\\oids\\runtime\\";
    	String savePath="E:\\experiment\\result\\Classification Accuracy and Runtime\\oids\\runtime_handle\\";
    	
    	String[] FileNames = getDataFileNames(openPath);
    	

        for(int i = 0; i < FileNames.length; i++){ 
        	
            System.out.println((i+1)+"\t"+FileNames[i]);
            double[][] times = readDataArrayInfor(openPath,FileNames[i]);
            double[][] modified_time = modify(times);
            
            String savePath_time = savePath + FileNames[i]+".txt";
            
            saveResults(savePath_time, modified_time);


}
    }
}