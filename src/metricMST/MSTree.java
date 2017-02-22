/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metricMST;

/**
 * The minimum spaning tree
 * @author String
 */
public class MSTree {
    protected int s_numOfEdges;         //the number of edges in the graph
    protected int s_numOfVertex;        //the number of vertex in the graph
    
    protected double[][] s_weightOfGraph; //the weights of the edges, upper triangular matrix

    protected double[] s_weightOfEdges;   //the weights of the edges, an single array
    protected int[] s_indexOfEdges;     //the index of the edges,corresponding to a upper triangular

    protected int[] s_edgesInMSTree;    //the edges appearing in the MSTree
    
    protected double s_maxWeightInTree; //the maximum weight appearing in MST
    protected double s_minWeightInTree; //the minimum weight appearing in MST
    protected double s_totalWeightInTree;//the sum of the weight appearing in MST

    protected int[] s_vertexSet;    //记录每个节点是属于那个分割
    protected int[] s_firstVertex;  //记录每个节点对应的首节点
    protected int[] s_nextVertex;   //记录每个节点对应的后续节点
    /**
     * The default constructor
     */
    public MSTree(){
    }
    /**
     * Constructor
     * @param weightArray
     * @param vertex
     */
    public MSTree(double[][] weightArray, int vertex){
        s_weightOfGraph = weightArray;
        s_numOfVertex = vertex;
    }

    public void intialParameters(){
        //complete graph
        s_numOfEdges = (s_numOfVertex - 1) * s_numOfVertex / 2;
        //weight of each edge
        s_weightOfEdges = new double[s_numOfEdges];
        //each edge has only one index which corresponding the
        s_indexOfEdges = new int[s_numOfEdges];
        //record the edges appearing in the minimum spanning tree
        s_edgesInMSTree = new int[s_numOfVertex-1];

        int index = 0;
        int inIndex = 0;
        for(int i = 0; i < s_numOfVertex; i++){
            for(int j = i+1; j < s_numOfVertex; j++){
                index = i*s_numOfVertex + j; //code of each edge
                s_weightOfEdges[inIndex] = s_weightOfGraph[i][j];
                s_indexOfEdges[inIndex] = index;
                inIndex++;
            }
        }

        for(int i = 0; i < s_edgesInMSTree.length; i++){
            s_edgesInMSTree[i] = 0;
        }

        s_minWeightInTree = 0;
        s_maxWeightInTree = 0;
        s_totalWeightInTree = 0;

        s_vertexSet = new int[s_numOfVertex];
        s_firstVertex = new int[s_numOfVertex];
        s_nextVertex = new int[s_numOfVertex];

        for(int i = 0; i < s_numOfVertex; i++){
            s_vertexSet[i] = i;
            s_firstVertex[i] = i;
            s_nextVertex[i] = -1;
        }
    }
    /**
     * Sort the edges according to their weights
     */
    public void sortWeights(){
        int len = s_weightOfEdges.length;
        for(int i = 0; i < len -1; i++){
            double temp = 0;
            int tempIndex = 0;
            boolean isExchanged = false;
            for(int j = len -1; j > i; j--){
                if(s_weightOfEdges[j]<s_weightOfEdges[j-1]){
                    temp = s_weightOfEdges[j];
                    s_weightOfEdges[j] = s_weightOfEdges[j-1];
                    s_weightOfEdges[j-1] = temp;

                    tempIndex = s_indexOfEdges[j];
                    s_indexOfEdges[j] = s_indexOfEdges[j-1];
                    s_indexOfEdges[j-1] = tempIndex;
                    isExchanged = true;
                }
            }

//            System.out.println("Pass"+i);
            if(!isExchanged){
                break;
            }
        }
    }

    public void quickSortWeights(){
        int start = 0;
        int end = s_weightOfEdges.length-1;

        quickSort(start, end);
    }

    public void quickSort(int start, int end){
        if(start < end){
            int median = partition(start, end);
            quickSort(start, median-1);
            quickSort(median+1, end);
        }
    }

    public int partition(int start, int end){
        double x = s_weightOfEdges[end];
        int i = start - 1;

        double temp = 0;
        int tempIndex = 0;

        for(int j = start; j < end; j++){
            if(s_weightOfEdges[j] <= x){
                i = i+1;

                temp = s_weightOfEdges[i];
                s_weightOfEdges[i] = s_weightOfEdges[j];
                s_weightOfEdges[j] = temp;

                tempIndex = s_indexOfEdges[i];
                s_indexOfEdges[i] = s_indexOfEdges[j];
                s_indexOfEdges[j] = tempIndex;
            }
        }

        temp = s_weightOfEdges[i+1];
        s_weightOfEdges[i+1] = s_weightOfEdges[end];
        s_weightOfEdges[end] = temp;

        tempIndex = s_indexOfEdges[i+1];
        s_indexOfEdges[i+1] = s_indexOfEdges[end];
        s_indexOfEdges[end] = tempIndex;
        
        return (i+1);
    }

    public void KruskalMST(){
        sortWeights();//排序
//        quickSortWeights();

        int edgeNumber = 0; 
        int tempIndex, left, right;
        int i, k, leftSet, rightSet, tempSet, tempFirst;

        i=0;

        s_minWeightInTree = s_weightOfEdges[0];
        while(i < s_numOfEdges && edgeNumber < (s_numOfVertex-1)){
            tempIndex = s_indexOfEdges[i];
            left = tempIndex/s_numOfVertex;
            right = tempIndex%s_numOfVertex;

            leftSet = s_vertexSet[left];
            rightSet = s_vertexSet[right];

//            System.out.println(leftSet+"\t"+rightSet);
            if(leftSet != rightSet){        //对应的边可以加入到当前的树中
                 s_edgesInMSTree[edgeNumber] = tempIndex;
                 s_totalWeightInTree = s_totalWeightInTree + s_weightOfEdges[i];

                 edgeNumber++;
                 k = left;
                 while(s_nextVertex[k]>=0){
                     k = s_nextVertex[k];
                 }
                 
                 s_nextVertex[k] = s_firstVertex[right];

                 tempFirst = s_firstVertex[left];
                 tempSet = s_vertexSet[left];

                 for(k = s_firstVertex[right]; k >=0; k = s_nextVertex[k]){
                     s_firstVertex[k] = tempFirst;
                     s_vertexSet[k] = tempSet;
                 }
            }
            i++;
        }

        s_maxWeightInTree = s_weightOfEdges[i-1];
    }

    public int[] getMST(){
        return s_edgesInMSTree;
    }

    public double getMaxWeight(){
        return s_maxWeightInTree;
    }

    public void showMST(){
        for(int i = 0; i < this.s_weightOfEdges.length; i++){
            System.out.println(s_weightOfEdges[i]+"\t"+s_indexOfEdges[i]);
        }
        System.out.println("Minumum Spanning Tree:");
        for(int i = 0; i < s_edgesInMSTree.length; i++){
            System.out.println(s_edgesInMSTree[i]);
        }

        System.out.println("The maximum weight in MST: "+s_maxWeightInTree);
        System.out.println("The total weight in MST: "+s_totalWeightInTree);
    }

    public static void main(String []args){
        double[][] array={{0,4,3,4},{0,0,4,4},{0,0,0,3},{0,0,0,0}};
        double[][] array0={{0,5,6,7},{0,0,8,9},{0,0,0,10},{0,0,0,0}};
        double[][] array1={{0,1,6,2,20,11},{0,0,3,20,20,9},{0,0,0,4,10,20},{0,0,0,0,5,7},{0,0,0,0,0,6},{0,0,0,0,0,0}};
        MSTree tree = new MSTree(array,4);
        tree.intialParameters();
        tree.KruskalMST();
        tree.showMST();
//        for(int i = 0; i < tree.s_numOfEdges; i++){
//            int index = tree.s_indexOfEdges[i];
//            int row = index/tree.s_numOfVertex;
//            int col = index%tree.s_numOfVertex;
//
//            System.out.println(index+" "+row+" "+col+" "+tree.s_weightOfEdges[i]);
//        }
    }
}
