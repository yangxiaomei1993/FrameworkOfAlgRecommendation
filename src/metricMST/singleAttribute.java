/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metricMST;

/**
 *
 * @author String
 */
public class singleAttribute {

    public String s_attrName;
    public String[] s_attrValues;
    public int s_numOfValues;
    public double[] s_countArray;
    public double s_totalNumber;
    public double s_entro;

    public singleAttribute(){
    }

    public singleAttribute(String name, String[] values){
        s_attrName = name;
        s_attrValues = values;
        s_numOfValues = values.length;
        s_countArray = new double[s_numOfValues];
        for(int i = 0; i < s_countArray.length; i++){
            s_countArray[i] = 0.0;
        }
    }

    public void update(int index){
        s_countArray[index]++;
        s_totalNumber++;
    }

    public void computeEntro(){
        double sumEntro = 0;
        double sum = 0;
        for(int i = 0; i < s_countArray.length; i++){
            if(s_countArray[i]!=0){
                sumEntro = sumEntro + s_countArray[i]*(Math.log(s_countArray[i])/Math.log(2.0));
                sum = sum + s_countArray[i];
            }
        }
        if(sum > 0){
            s_entro = fourDecimal(Math.log(sum)/Math.log(2) - sumEntro/sum);
        }else{
            s_entro = 0;
        }

//        System.out.println(s_attrName+"\t"+s_entro);
    }

    public double fourDecimal(double d){
        return Math.floor(d*10000+0.5)/10000;
    }
}
