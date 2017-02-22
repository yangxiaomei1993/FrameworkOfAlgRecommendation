package metaTarget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.sound.sampled.Line;

import fileUtil.fileHandle;
import fileUtil.fileOperator;

/** 
* @author  Yang Xiaomei 
* @date 2016年9月21日 上午10:09:54 
* @description  
*/
public class TestInput {

	public TestInput() {
		// TODO Auto-generated constructor stub
	}
		private static int numOfAlgs=5;
		private static int initialTime=3600;
		public static String[] getDataFileNames(String fileNamePath){
	        fileHandle fh = new fileHandle();
	        String[] fileNames = fh.getFileNames(fileNamePath, "txt", true);
	        return fileNames;
	    }
		

	    public static double[][] readDataArrayInfor(String fileNamePath, String dataName) throws IOException{
	        
	    	FileReader fr = new FileReader(fileNamePath + dataName + ".txt");
	    	BufferedReader br = new BufferedReader(fr);
	        double[][] data = new double[numOfAlgs][6];
	        
	        for(int i = 0; i < numOfAlgs; i++){
	            data[i] = new double[6];
	            for(int j = 0; j < data[i].length; j++){
	                data[i][j] = 0;
	            }
	        }

	        for(int i = 0; i < numOfAlgs; i++){
		    		double[] dataLine = new double[6];
					String line = br.readLine();

					String[] tokens = line.split(",");

					for (int j=0;j<6;j++)
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
	    	for (int i = 0;i < 6;i++)
	    	{
	    		if (data[i]==0)
	    			iszero = true;
	    				}
	    	return iszero;
		}//如果有0存在的话，返回true
	    
	    public static double[][] modify(double[][] data ) {
	    	for (int j = 0;j < numOfAlgs;j++)
	    	{
	    		if ( isZero(data[j]) )
	    		{
	    			for (int i = 0;i < 6; i++)
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
	            for(int j = 1; j < 6; j++){
	                line = line + "," + data[i][j];
	            }
	            fo.writeFile(line);
	        }

	        fo.closeWriteFile();
	    }

	    public static void main(String[] args) throws IOException{
	    	String openPath="C:\\Users\\Administrator\\Desktop\\test\\";
	    	String savePath="C:\\Users\\Administrator\\Desktop\\test\\";
	    	
	    	String[] FileNames = getDataFileNames(openPath);
	    	

	        for(int i = 0; i < FileNames.length; i++){ 
	        	
	            System.out.println((i+1)+"\t"+FileNames[i]);
	            double[][] times = readDataArrayInfor(openPath,FileNames[i]);
	            double[][] modified_time = modify(times);
	            
	            String savePath_time = savePath+"test_handle"+".txt";
	            
	            saveResults(savePath_time, modified_time);


}
	    }
}
