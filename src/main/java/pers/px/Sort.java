package pers.px;

import java.text.MessageFormat;
import java.util.*;

public class Sort {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int num=0;
        List source=new ArrayList();
        while (in.hasNextInt()) {
            if(num==0){
                num=in.nextInt();
                source=new ArrayList();
                continue;
            }
            source.add(in.nextInt());
            if(source.size()==num){
                new HashSet<>(source).stream().sorted().forEach(a-> System.out.println(a));
                num=0;
            }
        }
    }

    static int solution(int source){
        int quotient=source/3;
        int target=quotient;
        int remainder=source%3;
        while(quotient>0){
            System.out.println(MessageFormat.format("{0},{1},{2}",target,quotient,remainder));
            source=quotient+remainder;
            quotient=source/3;
            remainder=source%3;
            if(quotient+remainder==2){
                target++;
            }else {
                target+=quotient;
            }
        }
        return target;
    }
}
