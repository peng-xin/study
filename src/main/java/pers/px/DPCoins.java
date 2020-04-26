package pers.px;

import java.util.Arrays;

public class DPCoins {

    public static void coins(int target,int[] params){
        int[] dpArr=new int[target+1];
        int[] resArr=new int[target+1];
        int i=1;
        while (i<=target){
            int[] funArr=new int[params.length];
            for (int i1 = 0; i1 < params.length; i1++) {
                if (i - params[i1] >= 0) {
                    funArr[i1]=dpArr[i-params[i1]]+1;
                }
            }
            dpArr[i]= Arrays.stream(funArr).filter(num->num!=0).min().getAsInt();
            for (int i1 = 0; i1 < params.length; i1++) {
                if(params[i1]==dpArr[i]){
                    resArr[i]= i1;
                }
            }
            i++;
        }
        System.out.println(Arrays.toString(dpArr));
    }

    public static void cutStell(int target,int[] params){
        int[] dpArr=new int[target+1];
        int i=1;
        while (i<=target){
            int[] funArr=new int[params.length];
            for (int i1 = 0; i1 < params.length; i1++) {
                if(i-i1>=0){
                    funArr[i1]=dpArr[i-i1]+params[i1];
                }
            }
            dpArr[i++]= Arrays.stream(funArr).max().getAsInt();
        }
        System.out.println(Arrays.toString(dpArr));
    }
}
