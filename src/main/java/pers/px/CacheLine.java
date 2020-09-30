package pers.px;

public class CacheLine {
    public static void main(String[] args) {
        long[][] arr = new long[1024 * 1024] [8];
        long marked = System.currentTimeMillis();
        long sum=0;
        for (int i = 0; i < 1024 * 1024; i += 1) {
            for (int j = 0; j < 8; j++) {
                sum += arr[i][j];
            }
        }
        System.out.println("Loop times:" + (System.currentTimeMillis() - marked)+ "ms");

        marked = System.currentTimeMillis();
        sum=0;
        for (int i = 0; i < 8; i += 1) {
            for (int j = 0; j < 1024 * 1024; j++) {
                sum += arr[j][i];
            }
        }
        System.out.println("Loop times:" + (System.currentTimeMillis() - marked)+ "ms");
    }
}
