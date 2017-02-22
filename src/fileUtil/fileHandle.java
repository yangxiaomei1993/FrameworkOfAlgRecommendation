/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fileUtil;

import java.io.File;
import java.io.FileFilter;

/**
 *  Get the useful informations of file or file fold
 * @author String
 */
public class fileHandle {
    /**
     * 
     * @param path
     * @param condition
     * @return
     */
    public String[] getFileNames(String path, final String condition){
        return getFileNames(path, condition, false);
    }
/**
 * @param path
 * @param condition
 * @param trim
 * @return
 */
    public String[] getFileNames(String path, final String condition, boolean trim){
        File root = new File(path);
        if(!root.isDirectory()){
            System.out.println("The path should be a ditectory!!! Wrong format "+ path);
            System.exit(1);
        }
        FileFilter fileFilter = new FileFilter(){
            public boolean accept(File fpoint){
                String temp = fpoint.getName().trim().toLowerCase();
                if(temp.endsWith(condition.toLowerCase())){
                    return true;
                }else{
                    return false;
                }
            }
        };
        File files[] = root.listFiles(fileFilter);
        String fileNames[] = new String[files.length];
        for(int i = 0; i < fileNames.length; i++){
            String temp = files[i].getName();
            if(trim){
               fileNames[i] = temp.substring(0,temp.length()-condition.length()-1);
            }else{
               fileNames[i] = temp;
            }

        }
        return fileNames;
    }
/**
 *
 * @param path
 * @param src
 * @param target
 */
    public void fileRename(String path, String src, String target){
        String filePath = path + "\\" + src;
        File file = new File(filePath);
        if(!file.isFile()||!file.exists()){
            System.out.println(filePath + "is not a file name!!!");
            System.exit(1);
        }
        File fileAnother = new File(path + "\\" + target);
        file.renameTo(fileAnother);
    }
/**
 * 
 * @param path
 * @param fileName
 * @return
 */
    public boolean delFile(String path, String fileName){
        File file = new File(path + "\\" + fileName);
        if(!file.exists()||!file.isFile()){
            System.out.println("File " + fileName + " is not exist!!!");
            System.exit(1);
        }
        boolean flag = false;
        try{
            flag = file.delete();
        }catch(java.io.IOError e){
            System.out.println(e.getMessage());
        }               
        return flag;
    }
    
    public static void main(String []args){
        fileHandle fh = new fileHandle();
        String names[] = fh.getFileNames("F:\\resource\\Paper Drafts\\PaperDrafts FS_Based on AssociationRules\\data set\\UCI\\only_nominal_attribute_reduction\\CFS", "arff", true);
        String path = "F:\\program\\FEAST\\build\\classes\\result\\";
        for(int i = 0; i < names.length; i++){
            System.out.println("load (\'" + path + names[i] + ".txt\')");
        }
//        if(fh.delFile("C:\\Documents and Settings\\String\\桌面", "ww.txt"))
//           System.out.println("Delete file succcessfully!!");
    }
}
