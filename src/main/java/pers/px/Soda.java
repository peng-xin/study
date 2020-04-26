package pers.px;

import java.text.MessageFormat;

public class Soda {
    public static void main(String[] args) {
        System.out.println(solution(16));
        System.out.println(solution(85));
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
