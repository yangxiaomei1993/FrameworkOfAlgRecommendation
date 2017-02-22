/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metricMST;

/**
 *
 * @author String
 */
public class AttributePairs {

    protected singleAttribute s_attri;
    protected singleAttribute s_attrj;
    protected double[][] s_countArray;
    protected double s_totalCount;
    protected double s_entro;
    protected double[] s_countArrayi;
    protected double[] s_countArrayj;
    protected double s_condEntroi;
    protected double s_condEntroj;
    protected double s_mulInfor;

    public AttributePairs() {
    }

    public AttributePairs(singleAttribute attri, singleAttribute attrj) {
        s_attri = attri;
        s_attrj = attrj;

        s_countArray = new double[s_attri.s_numOfValues][];
        for (int i = 0; i < s_countArray.length; i++) {
            s_countArray[i] = new double[s_attrj.s_numOfValues];
            for (int j = 0; j < s_countArray[i].length; j++) {
                s_countArray[i][j] = 0;
            }
        }

        s_countArrayi = new double[s_attri.s_numOfValues];
        for (int i = 0; i < s_countArrayi.length; i++) {
            s_countArrayi[i] = 0;
        }
        s_countArrayj = new double[s_attrj.s_numOfValues];
        for (int i = 0; i < s_countArrayj.length; i++) {
            s_countArrayj[i] = 0;
        }

        s_totalCount = 0;
        s_entro = 0;
        s_condEntroi = 0;
        s_condEntroj = 0;
    }

    public void update(int indexi, int indexj) {
        s_countArray[indexi][indexj]++;
        s_countArrayi[indexi]++;
        s_countArrayj[indexj]++;
        s_totalCount++;
    }

    public void computeEntro() {
        double sum = 0;
        
        for (int i = 0; i < s_countArray.length; i++) {
            for (int j = 0; j < s_countArray[i].length; j++) {
                if (s_countArray[i][j] > 0) {
                    sum = sum + fourDecimal(s_countArray[i][j] * Math.log(s_countArray[i][j]) / Math.log(2));
                }
            }
        }
        s_entro = fourDecimal(Math.log(s_totalCount) / Math.log(2) - sum / s_totalCount);
        computeConditionEntro();        
    }

    public void computeConditionEntro() {
        double sum = 0;
        for (int i = 0; i < s_countArrayi.length; i++) {
            if (s_countArrayi[i] > 0) {
                sum = sum + fourDecimal(s_countArrayi[i] * Math.log(s_countArrayi[i]) / Math.log(2));
            }
        }
        s_condEntroi = fourDecimal(Math.log(s_totalCount) / Math.log(2) - sum / s_totalCount);
        s_condEntroi = fourDecimal(s_entro - s_condEntroi);

        sum = 0;
        for (int i = 0; i < s_countArrayj.length; i++) {
            if (s_countArrayj[i] > 0) {
                sum = sum + fourDecimal(s_countArrayj[i] * Math.log(s_countArrayj[i]) / Math.log(2));
            }
        }
        s_condEntroj = fourDecimal(Math.log(s_totalCount) / Math.log(2) - sum / s_totalCount);
        s_condEntroj = fourDecimal(s_entro - s_condEntroj);
    }

    public void computeMultualInfor(){
        double sumEntro = 0;
        double sum = 0;
        double hi = 0;
        double hj = 0;


        for (int i = 0; i < s_countArray.length; i++) {
            for (int j = 0; j < s_countArray[i].length; j++) {
                if (s_countArray[i][j] > 0) {
                    sumEntro = sumEntro + s_countArray[i][j] * Math.log(s_countArray[i][j]) / Math.log(2.0);
                    sum = sum + s_countArray[i][j];
                }
            }
        }

        for (int i = 0; i < s_countArrayi.length; i++) {
            if (s_countArrayi[i] > 0) {
                hi = hi + s_countArrayi[i] * Math.log(s_countArrayi[i]) / Math.log(2.0);
            }
        }

        for (int i = 0; i < s_countArrayj.length; i++) {
            if (s_countArrayj[i] > 0) {
                hj = hj + s_countArrayj[i] * Math.log(s_countArrayj[i]) / Math.log(2.0);
            }
        }
//        System.out.println(sum+"\t"+s_attri.s_attrName+"\t"+hj+"\t"+s_attrj.s_attrName+"\t"+sumEntro);
        if (sum == 0) {
            s_mulInfor = 0;
            return;
        }
        double temp = Math.log(sum) / Math.log(2.0) + (sumEntro - hi - hj) / sum;

        s_mulInfor = fourDecimal(temp);
    }
    

    public double fourDecimal(double d) {
        return Math.floor(d * 10000 + 0.5) / 10000;
    }
}
