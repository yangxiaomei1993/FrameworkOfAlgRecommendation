package com.mycode;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
 
import java.io.File;

import fileUtil.fileHandle;
/** 
* @author  Yang Xiaomei 
* @date 2016年8月15日 下午7:05:45 
* @description  
*/
public class Csv2Arff {

	public Csv2Arff() {
		// TODO Auto-generated constructor stub
	}
	  /**
	   * takes 2 arguments:
	   * - CSV input file
	   * - ARFF output file
	   */
	  public static void main(String[] args) throws Exception {
		  
//		    String openFilePath = "E:\\experiment\\dataset\\UCI\\csv\\";
//
//	        String saveFilePath = "E:\\experiment\\dataset\\UCI\\csv2arff\\";

//	        fileHandle fh = new fileHandle();
//	        String[] fileNames = fh.getFileNames(openFilePath, "csv", true);

//	        for(int i = 0; i < fileNames.length; i++){
//	        	
//	            System.out.println(fileNames[i]);
//	            String openFileName=openFilePath + fileNames[i] + ".csv";
//	  		    String saveFileName=saveFilePath + fileNames[i] + ".arff";
	        
		  String openFileName="E:\\experiment\\dataset\\UCI\\csv\\covtype.csv";
		  String saveFileName="E:\\experiment\\dataset\\UCI\\covtype.arff";
		  
	  		  // load CSV
	  		    CSVLoader loader = new CSVLoader();
	  		    loader.setSource(new File(openFileName));
	  		    Instances data = loader.getDataSet();
	  		 
	  		    // save ARFF
	  		    ArffSaver saver = new ArffSaver();
	  		    saver.setInstances(data);
	  		    saver.setFile(new File(saveFileName));
//	  		    saver.setDestination(new File(saveFileName));
	  		    saver.writeBatch();
	  		  }
	            
		  
//	    if (args.length != 2) {
//	      System.out.println("\nUsage: CSV2Arff <input.csv> <output.arff>\n");
//	      System.exit(1);
//	    }
	 
	  }
	



