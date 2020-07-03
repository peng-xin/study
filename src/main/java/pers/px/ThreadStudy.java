package pers.px;

import java.util.Random;

public class ThreadStudy {
    private static Random random = new Random(10000);

    public static void main(String[] args) {
        Task task = new Task();
        int[] arr = new int[20];
        while (true) {
            int i = 0;
            while (i < 20) {
                arr[i++] = random.nextInt();
            }
            task.setArr(arr);

            task.start();
        }


    }

}

class Task extends Thread {

    int[] arr = new int[20];

    public void setArr(int[] arr) {
        this.arr = arr;
    }

    @Override
    public void run() {
        for (int i = 1; i <= arr.length; i++) {
            System.out.print(arr[i - 1]);
            System.out.print(" ");
            if (i % 10 == 0) {
                System.out.println();
                try {
                    Thread.sleep(24L);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}