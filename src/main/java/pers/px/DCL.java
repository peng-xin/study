package pers.px;

import java.util.Date;

public class DCL {
    public static Date date;

    public void initDate() {
        if (null == date) {
            synchronized (this) {
                if (null == date) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    date = new Date();
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        DCL dcl = new DCL();
        int i = 0;
        while (i++ < 1000) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + ">>" + date.hashCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dcl.initDate();
                System.out.println(Thread.currentThread().getName() + ">>" + date.hashCode());
            }).start();
        }
    }
}
