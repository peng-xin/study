package pers.px;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Singleton {
    private static void bubbleSort(Integer[] source){
        if (source==null){
            System.out.println("数组为空");
        }
        for (int i=0;i<source.length;i++){
            for (int j=0;j<source.length-1-i;j++){
                if (source[j]>source[j+1]){
                    source[j]^=source[j+1];
                    source[j+1]^=source[j];
                    source[j]^=source[j+1];
                }
            }
            System.out.println(Arrays.toString(source));
        }
    }

    private static void selectionSort(Integer[] source){
        if(source==null){
            System.out.println("数组为空");
        }
        for (int i=0;i<source.length-1;i++){
            int chosen=i;
            for (int j=1;j<source.length;j++){
                if(source[i]>source[j]){
                    chosen=j;
                }
            }
            if(chosen!=i){
                source[i]^=source[chosen];
                source[chosen]^=source[i];
                source[i]^=source[chosen];
            }
            System.out.println(Arrays.toString(source));
        }
    }

    private static void insertSort(Integer[] source){
        if(source==null){
            System.out.println("数组为空");
        }
        for (int i=1;i<source.length;i++){
            for (int j=i-1;j>0;j--){
                if(source[j]<source[j-1]){
                    source[j]^=source[j-1];
                    source[j-1]^=source[j];
                    source[j]^=source[j-1];
                }
            }
            System.out.println(Arrays.toString(source));
        }
    }

    private static List<Integer> mergeSort(List<Integer> source){
        if(source.size()<2){
            return source;
        }
        return merge(mergeSort(source.subList(0,source.size()>>>1)),mergeSort(source.subList(source.size()>>>1,source.size())));
    }

    private static List<Integer> merge(List<Integer> left, List<Integer> right){
        List result=new ArrayList();
        Integer offsetLeft=0;
        Integer offsetRight=0;
        while (offsetLeft<left.size()&&offsetRight<right.size()){
            if(left.get(offsetLeft)<right.get(offsetRight)){
                result.add(left.get(offsetLeft++));
            }else {
                result.add(right.get(offsetRight++));
            }
        }
        while (offsetLeft<left.size()){
            result.add(left.get(offsetLeft++));
        }
        while (offsetRight<right.size()){
            result.add(right.get(offsetRight++));
        }
        return result;
    }

    private static void heapify(Integer[] source,Integer offset,Integer length){
        if((offset<<1)+1<length&&source[offset]<source[(offset<<1)+1]){
            source[offset]^=source[(offset<<1)+1];
            source[(offset<<1)+1]^=source[offset];
            source[offset]^=source[(offset<<1)+1];
            heapify(source,(offset<<1)+1,length);
        }
        if ((offset<<1)+2<length&&source[offset]<source[(offset<<1)+2]){
            source[offset]^=source[(offset<<1)+2];
            source[(offset<<1)+2]^=source[offset];
            source[offset]^=source[(offset<<1)+2];
            heapify(source,(offset<<1)+2,length);
        }
    }

    public static void main(String[] args){
        Random random=new Random();
        Integer[] source=new Integer[10];
        for (int i=0;i<10;i++){
            source[i]=random.nextInt(10);
        }
        System.out.println("初始数据");
        System.out.println(Arrays.toString(source));
        System.out.println("冒泡排序");
        bubbleSort(source);
        for (int i=0;i<10;i++){
            source[i]=random.nextInt(10);
        }
        System.out.println("初始数据");
        System.out.println(Arrays.toString(source));
        System.out.println("选择排序");
        selectionSort(source);
        for (int i=0;i<10;i++){
            source[i]=random.nextInt(10);
        }
        System.out.println("初始数据");
        System.out.println(Arrays.toString(source));
        System.out.println("插入排序");
        insertSort(source);
        for (int i=0;i<10;i++){
            source[i]=random.nextInt(10);
        }
        System.out.println("初始数据");
        System.out.println(Arrays.toString(source));
        System.out.println("归并排序");
        System.out.println(mergeSort(Arrays.asList(source)).toString());
        for (int i=0;i<10;i++){
            source[i]=random.nextInt(10);
        }
        System.out.println("初始数据");
        System.out.println(Arrays.toString(source));
        System.out.println("堆排序");
        for (int i=(source.length>>1)-1;i>=0;i--){
            heapify(source,i,source.length);
        }
        System.out.println(Arrays.toString(source));
        for (int i=source.length-1;i>0;i--){
            source[0]^=source[i];
            source[i]^=source[0];
            source[0]^=source[i];
            heapify(source,0,i);
        }
        System.out.println(Arrays.toString(source));
        ExecutorService executorService=Executors.newFixedThreadPool(10);
    }
}
