package pers.px;

public class ConvertToTitle {
    public static String convertToTitle(int n) {
        StringBuilder result = new StringBuilder();
        int m=n;
//        while (n > 0) {
//            if(n==26){
//                result.append((char) (65 + (n - 1) % 26));
//                break;
//            }
//            result.append((char) (65 + (n - 1) % 26));
//            n = n / 26;
//        }
//        System.out.println(result.reverse().toString());
//
//        result = new StringBuilder();
        while (m > 0) {
            if(result.length()>0){
                result.append((char) (65 + (--m) % 26));
            }else {
                result.append((char) (65 + m % 26));
            }
            m = m / 26;
        }
        return result.reverse().toString();
    }
}
