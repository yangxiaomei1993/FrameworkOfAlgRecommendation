/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author STRING
 */
public class fileOperator {
    /** Creates a new instance of fileOperator */
    public fileOperator() {
    }
    public FileWriter fw;
    public FileReader fr;
    public BufferedReader br;
    public BufferedWriter bw;
//  Open the read file,initial the read file pointer  
    public void openReadFile(String fileName) {
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

//  Close the read pointer   
    public void closeReadFile() {
        try {
            br.close();
            fr.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

// Open the write file Initial the write pointer
    public void openWriteFile(String fileName) {
        try {
            fw = new FileWriter(fileName, false);
            bw = new BufferedWriter(fw);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
//Close the write pointer    
    public void closeWriteFile() {
        try {
            bw.close();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
// Read a line from the read file   
    public String readByLine() {
        String temp = null;
        try {
        	
            temp = br.readLine();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return temp;
    }
 // Read a line from the read file   
    public String readByLine(int lineNum) {
        String temp = null;
        for(int i = -1; i < lineNum; i++ )
        {
        	try {
            	
                temp = br.readLine();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return temp;
    }
//Write a String to the write file as a line    
    public void writeFile(String line) {
        try {
            bw.write(line + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
