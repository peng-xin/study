package pers.px;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ThreadStudy {
    private static Random random = new Random(100);

    public static void main(String[] args) {
        Task task = new Task();
        List data = new ArrayList(20);
        task.setRunning(true);
        task.setData(data);

        task.start();
        while (true) {
            data = new ArrayList(20);
            System.out.println("while");
            int i = 0;
            while (i < 20) {
                data.add(random.nextInt(100));
                i++;
            }
            System.out.println("set");
            System.out.println(Arrays.toString(data.toArray()));
            task.setRunning(true);
            task.setData(data);
            if (data.contains(23)) {
                task.setRunning(false);
                System.out.println(Thread.currentThread() + "change");
                break;
            }
        }


        while (true) {
        }
    }

}

class Task extends Thread {
    private boolean running = true;

    private List data = new ArrayList(20);


    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setData(List data) {
        this.data = data;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread() + " running begin " + System.currentTimeMillis());
        while (running) {
            System.out.println(System.currentTimeMillis());
            try {
                if (data.contains(23)) {
                    running = false;
                    System.out.println(Thread.currentThread() + "change");
                }
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(System.currentTimeMillis());
//        for (int i = 1; i <= arr.length; i++) {
//            System.out.print(arr[i - 1]);
//            System.out.print(" ");
//            if (i % 10 == 0) {
//                System.out.println();
//                try {
//                    Thread.sleep(2000L);
//                } catch (InterruptedException e) {
//                    System.out.println(e.getMessage());
//                }
//            }
        }
        System.out.println(Thread.currentThread() + " running end " + System.currentTimeMillis());
    }
}