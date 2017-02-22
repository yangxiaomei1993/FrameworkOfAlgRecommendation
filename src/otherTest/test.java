/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package otherTest;

import java.io.File;
import java.io.IOException;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

/**
 *
 * @author String
 */
public class test {
    
    public static String readFilePath =  "F:\\resource\\数据挖掘算法的选择推荐\\real world data sets\\" +
                "classification problems\\Discretize_Fayyad_Irani\\dataset\\";
    public static String saveFilePath = "E:\\Phd paper experimental results\\data sets\\";
    
    public static String[] fileNames = {"ada_agnostic",
"ada_prior",
"anneal",
"anneal_ORIG",
"AR10P_130_674",
"arrhythmia",
"audiology",
"autos",
"balance-scale",
"breast-cancer",
"breast-w",
"bridges_version1",
"bridges_version2",
"car",
"CLL-SUB-111_111_2856",
"cmc",
"colic.ORIG",
"colic",
"colon",
"credit-a",
"credit-g",
"cylinder-bands",
"dermatology",
"diabetes",
"ECML90x27679",
"ecoli",
"Embryonaldataset_C",
"eucalyptus",
"flags",
"GCM_Test",
"gina_agnostic",
"gina_prior",
"gina_prior2",
"glass",
"grub-damage",
"heart-c",
"heart-h",
"heart-statlog",
"hepatitis",
"hypothyroid",
"ionosphere",
"iris",
"kdd_ipums_la_97-small",
"kdd_ipums_la_98-small",
"kdd_ipums_la_99-small",
"kdd_JapaneseVowels_test",
"kdd_JapaneseVowels_train",
"kdd_synthetic_control",
"kr-vs-kp",
"labor",
"Leukemia",
"Leukemia_3c",
"leukemia_test_34x7129",
"leukemia_train_38x7129",
"lung-cancer",
"lymph",
"Lymphoma45x4026+2classes",
"Lymphoma96x4026+10classes",
"Lymphoma96x4026+9classes",
"mfeat-fourier",
"mfeat-morphological",
"mfeat-pixel",
"mfeat-zernike",
"molecular-biology_promoters",
"monks-problems-1_test",
"monks-problems-1_train",
"monks-problems-2_test",
"monks-problems-2_train",
"monks-problems-3_test",
"monks-problems-3_train",
"mushroom",
"oh0.wc",
"oh10.wc",
"oh15.wc",
"oh5.wc",
"pasture",
"pendigits",
"PIE10P_210_1520",
"postoperative-patient-data",
"primary-tumor",
"segment",
"shuttle-landing-control",
"sick",
"SMK-CAN-187_187_1815",
"solar-flare_1",
"solar-flare_2",
"sonar",
"soybean",
"spectf_test",
"spectf_train",
"spectrometer",
"spect_test",
"spect_train",
"splice",
"sponge",
"squash-stored",
"squash-unstored",
"sylva_agnostic",
"sylva_prior",
"TOX-171_171_1538",
"tr11.wc",
"tr12.wc",
"tr23.wc",
"tr31.wc",
"tr41.wc",
"tr45.wc",
"trains",
"vehicle",
"vote",
"vowel",
"wap.wc",
"waveform-5000",
"white-clover",
"wine",
"zoo",
};
    
    public static void main(String[] args) throws IOException{
         for(int i = 0; i < fileNames.length; i++){
             String readFileName = readFilePath + fileNames[i] + ".arff";
             System.out.println(readFileName);
             Instances data = readArffDataSet(readFileName);
            
             saveDataset(data, fileNames[i]);
         }
         
         
    }
    
   public static Instances readArffDataSet(String fileName) throws IOException{
        ArffLoader arffLoader = new ArffLoader();
        File file = new File(fileName);

        arffLoader.setFile(file);
        Instances data = arffLoader.getDataSet();

        
        data.setClassIndex(data.numAttributes()-1);
        data.deleteAttributeAt(0);
        
        return data;
    }
    
    public static void saveDataset(Instances dataset, String fileName) throws IOException{
        ArffSaver arffSaver = new ArffSaver();
        File file = new File(saveFilePath+fileName+".arff");

        arffSaver.setFile(file);
        arffSaver.setInstances(dataset);
        arffSaver.writeBatch();
    }    
}
