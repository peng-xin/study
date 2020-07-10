package pers.px;

public class SpiralOrder {

    public static int[] solution(int[][] matrix) {
        int rowSize=matrix.length;
        int colSize=matrix[0].length;
        int[] result=new int[rowSize*colSize];
        int offset=0;
        while(offset<result.length){

        }
        return result;
    }

    private static int[] readTop(int[][] matrix,int rowIndex,int colBegin,int colEnd){
        int len=colEnd-colBegin;
        int offset=0;
        int[] result=new int[len];
        while (len-offset>0){
            result[offset]=matrix[rowIndex][colBegin+offset++];
        }
        return result;
    }
}
