package pers.px;

import com.mysql.jdbc.StringUtils;

import java.util.Arrays;

public class Kmp {
    public static int kmpSearch(String stringA,String stringB){
        if(StringUtils.isNullOrEmpty(stringA)||StringUtils.isNullOrEmpty(stringB)){
            return 0;
        }
        return stringA.length()>stringB.length()?kmp(stringA,stringB):kmp(stringB,stringA);
    }

    public static int kmp(String stringA,String stringB){
        int lengthA=stringA.length(),lengthB=stringB.length();
        int indexA=0,indexB=0;
        int[] next=makeNext(stringB);
        System.out.println(Arrays.toString(next));
        while (indexA<lengthA){
            if(indexB==-1||stringA.charAt(indexA)==stringB.charAt(indexB)){
                indexA++;indexB++;
            }else {
                indexB=next[indexB];
            }
            if(indexB==lengthB){
                return indexA-indexB;
            }
            System.out.println(indexA+"==>"+indexB);
        }
        return 0;
    }

    public static int[] makeNext(String nextString){
        int length=nextString.length();
        int[] result=new int[length];
        int index=0,next=-1;
        result[index]=next;
        while (index<length-1){
            if(next==-1||nextString.charAt(index)==nextString.charAt(next)){
                index++;next++;
                result[index]=next;
            }else {
                next=result[next];
            }
            System.out.println(index+"=>"+next+"=>"+result[index]);
        }
        return result;
    }
}
