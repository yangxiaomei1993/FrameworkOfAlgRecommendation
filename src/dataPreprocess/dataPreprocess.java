/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dataPreprocess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import fileUtil.fileHandle;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

/**
 * ����Ԥ����ɾ��Ŀ�����ȱʧ��ʵ��
 * �е����ݼ�Ŀ������ǵ�һ�����ԣ��е����ݼ���Ŀ����������һ������
 * @author String
 */
public class dataPreprocess {

    public static Instances readArff(String fileName) throws IOException{
        File file = new File(fileName);
        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setFile(file);
        Instances data = arffLoader.getDataSet();

        return data;
    }

    public static Instances removeMissClass(Instances data){
        Instances copy = new Instances(data, 0, data.numInstances());
        int classIndex = data.classIndex();

        copy.setClassIndex(classIndex);

        copy.deleteWithMissingClass();

        return copy;
    }

    public static void saveArffData(Instances data, String fileName) throws IOException{
        File file = new File(fileName);
        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setFile(file);
        arffSaver.setInstances(data);
        arffSaver.writeBatch();
    }

    public static void showAttriInfor(Instances data){
    	
    	int instance_num = 0;
    	int target_num = 0;
    	
        Attribute attr1st = data.attribute(0);
        Attribute attrlast = data.attribute(data.numAttributes() - 1);

        int[] counts = data.attributeStats(data.numAttributes() - 1).nominalCounts;
        //ͳ��ÿһ���ʵ����Ŀ
        target_num = counts.length;
        for (int i:counts)
        {
        	instance_num = instance_num + i;
        	if (i == 0)
        		{
        		target_num = target_num - 1;
        		}
        }
        
        //System.out.println(attr1st.name() + "\t" + attrlast.name() + "======>" + Arrays.toString(counts)+ 
        //		"\t" + "instance=" + instance_num + "\t" + "target=" + target_num);
        
        System.out.println(instance_num + "," + target_num);
    }

    public static void main(String[] args) throws IOException{
        String filePath = "E:\\experiment\\dataset\\UCI\\dataSetOids\\done\\";
        
		FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\data.csv",true);
		PrintStream ps = new PrintStream(out);
		System.setOut(ps);

        fileHandle fh = new fileHandle();
        String[] fileNames = fh.getFileNames(filePath, "arff");
        for(int i = 0; i < fileNames.length; i++){
//            System.out.println(i+"\t"+ fileNames[i] + "\t=>\t");
            String fileName = filePath + fileNames[i];
            Instances data = readArff(fileName);
            data.setClassIndex(data.numAttributes()-1);
//            Instances delData = removeMissClass(data);
//            System.out.println(i+"\t"+ (data.numInstances() - delData.numInstances()));
            System.out.print(i + "," + fileNames[i] + ",");
            showAttriInfor(data);
        }
    }
}
