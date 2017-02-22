/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metricMST;

/**
 *
 * @author String
 */
public class rapidMSTree {

    protected int s_numOfEdges;         //the number of edges in the graph
    protected int s_numOfVertex;        //the number of vertex in the graph
    protected double[][] s_weightOfGraph; //the weights of the edges, upper triangular matrix
    protected double[] s_weightOfEdges;   //the weights of the edges, an single array
    protected double[] s_weightHeap;    //采用堆排序的方法来找最大值

    protected int s_heapSize;
    protected int[] s_indexOfEdges;     //the index of the edges,corresponding to a upper triangular
    protected int[] s_edgesInMSTree;    //the edges appearing in the MSTree

    protected double s_maxWeightInTree; //the maximum weight appearing in MST
    protected double s_minWeightInTree; //the minimum weight appearing in MST
    protected double s_totalWeightInTree;//the sum of the weight appearing in MST

    protected int[] s_vertexSet;    //记录每个节点是属于那个分割
    protected int[] s_firstVertex;  //记录每个节点对应的首节点
    protected int[] s_nextVertex;   //记录每个节点对应的后续节点

    public rapidMSTree() {
    }

    public rapidMSTree(double[][] distances, int vertex) {
        s_weightOfGraph = distances;
        s_numOfVertex = vertex;
    }

    public void intialParameters() {
        s_numOfEdges = (s_numOfVertex - 1) * s_numOfVertex / 2;
        //将二维距离矩阵转化为一维距离数组
        s_weightOfEdges = new double[s_numOfEdges];
        s_weightHeap = new double[s_numOfEdges];
        //记录相应的下标，这个下标可以很方便的获得两个实例在原始数据集中的位置
        s_heapSize = s_numOfEdges;
        s_indexOfEdges = new int[s_numOfEdges];
        //记录出现在最小生成树中的边的位置
        s_edgesInMSTree = new int[s_numOfVertex - 1];

        int index = 0;
        int inIndex = 0;
        //将二维矩阵中的距离转化为一维数组中的值
        for (int i = 0; i < s_numOfVertex; i++) {
            for (int j = i + 1; j < s_numOfVertex; j++) {
                index = i * s_numOfVertex + j; //code of each edge
                s_weightOfEdges[inIndex] = s_weightOfGraph[i][j];
                s_weightHeap[inIndex] = s_weightOfGraph[i][j];
                s_indexOfEdges[inIndex] = index;
                inIndex++;
            }
        }

        for (int i = 0; i < s_edgesInMSTree.length; i++) {
            s_edgesInMSTree[i] = 0;
        }

        s_maxWeightInTree = 0;
        s_minWeightInTree = 0;
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

    public void constructMinHeap() {
        if(s_heapSize == 1){
            System.out.println("There is only one vlaue in this array!!");
            return;
        }
        int midNodeNum = s_heapSize/2-1;
        for(int index = midNodeNum; index >= 0; index--){
            minHeapify(index);
        }
    }

    public void minHeapify(int cindex) {
        int left = getLeftChild(cindex);
        if (left >= s_heapSize) {
            return;
        }

        int smallestIndex = -1;
        if (s_weightHeap[left] < s_weightHeap[cindex]) {
            smallestIndex = left;
        } else {
            smallestIndex = cindex;
        }

        int right = left + 1;
        if (right < s_heapSize && s_weightHeap[right] < s_weightHeap[smallestIndex]) {
            smallestIndex = right;
        }

        if (smallestIndex != cindex) {
            double temp = 0;
            temp = s_weightHeap[cindex];
            s_weightHeap[cindex] = s_weightHeap[smallestIndex];
            s_weightHeap[smallestIndex] = temp;

            int tempIndex = 0;
            tempIndex = s_indexOfEdges[cindex];
            s_indexOfEdges[cindex] = s_indexOfEdges[smallestIndex];
            s_indexOfEdges[smallestIndex] = tempIndex;
            minHeapify(smallestIndex);
        }
    }

    public void heapExtractMinimum(){
        if(s_heapSize<1){
            return;
        }
        
        double temp = s_weightHeap[0];
        s_weightHeap[0] = s_weightHeap[s_heapSize-1];
        s_weightHeap[s_heapSize-1] = temp;//和末尾的数值互换，然后再进行调整

        int tempIndex = s_indexOfEdges[0];
        s_indexOfEdges[0] = s_indexOfEdges[s_heapSize-1];
        s_indexOfEdges[s_heapSize-1] = tempIndex;

        s_heapSize--;
        
        minHeapify(0);
    }

    public int getLeftChild(int parent) {
        return 2 * parent + 1;
    }

    public int getParent(int child) {
        return (child + 1) / 2 - 1;
    }

    public void RapidKruskalMST(){
        constructMinHeap();
        
        int edgeNumber = 0;
        int tempIndex, left, right;
        int i, leftSet, rightSet, tempSet, tempFirst;

        s_minWeightInTree = s_weightHeap[0];


        while(s_heapSize > 0 && edgeNumber < (s_numOfVertex-1)){
            tempIndex = s_indexOfEdges[0];
            left = tempIndex / s_numOfVertex;
            right = tempIndex % s_numOfVertex;

//            System.out.println(s_weightHeap[0]);

            leftSet = s_vertexSet[left];
            rightSet = s_vertexSet[right];

            if(leftSet != rightSet){
                s_edgesInMSTree[edgeNumber] = tempIndex;
                s_totalWeightInTree = s_totalWeightInTree + s_weightHeap[0];

                edgeNumber++;
                i = left;
                while(s_nextVertex[i]>=0){
                    i = s_nextVertex[i];
                }

                s_nextVertex[i] = s_firstVertex[right];

                tempFirst = s_firstVertex[left];
                tempSet = s_vertexSet[left];

                for (i = s_firstVertex[right]; i >= 0; i = s_nextVertex[i]) {
                    s_firstVertex[i] = tempFirst;
                    s_vertexSet[i] = tempSet;
                }
            }
            heapExtractMinimum();
        }

        s_maxWeightInTree = s_weightOfEdges[s_heapSize];
    }

    public int[] getEdgeIndexes(){
        return s_edgesInMSTree;
    }
    
    public void showRapidMSTree(){
//        for(int i = 0; i < s_numOfEdges; i++){
//            System.out.println(s_weightHeap[i]);
//        }

        System.out.println("Minumum Spanning Tree:");
        for (int i = 0; i < s_edgesInMSTree.length; i++) {
            System.out.println(s_edgesInMSTree[i]);
        }

        System.out.println("The maximum weight in MST: " + s_maxWeightInTree);
        System.out.println("The total weight in MST: " + s_totalWeightInTree);
//        System.out.println(s_numOfEdges/2-1);
//        for(int i = 0; i <= s_numOfEdges/2-1; i++){
//            int left = getLeftChild(i);
//            System.out.print(s_weightHeap[i]+"\t"+s_weightHeap[left]+"\t");
//            if((left+1)<s_weightHeap.length){
//                System.out.println(s_weightHeap[left+1]);
//            }else{
//                System.out.println();
//            }
//        }
    }

    public static void main(String []args){
        double[][] array={{0,10,9,8},{0,0,7,6},{0,0,0,5},{0,0,0,0}};
        double[][] array0={{0,5,6,7},{0,0,8,9},{0,0,0,10},{0,0,0,0}};
        double[][] array1={{0,1,6,2,20,11},{0,0,3,20,20,9},{0,0,0,4,10,20},{0,0,0,0,5,7},{0,0,0,0,0,6},{0,0,0,0,0,0}};
        rapidMSTree tree = new rapidMSTree(array,4);
        tree.intialParameters();
        tree.constructMinHeap();
        tree.RapidKruskalMST();
        tree.showRapidMSTree();
//        for(int i = 0; i < tree.s_numOfEdges; i++){
//            int index = tree.s_indexOfEdges[i];
//            int row = index/tree.s_numOfVertex;
//            int col = index%tree.s_numOfVertex;
//
//            System.out.println(index+" "+row+" "+col+" "+tree.s_weightOfEdges[i]);
//        }
    }
}
