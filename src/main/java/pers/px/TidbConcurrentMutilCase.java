package pers.px;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TidbConcurrentMutilCase extends Thread{
    private CountDownLatch countDownLatch;
    public TidbConcurrentMutilCase(CountDownLatch countDownLatch) {
        this.countDownLatch=countDownLatch;
    }

    public void run() {
        long s = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + "准备执行");
        List<String> sqls=getRequestSqls_6();
        //串行
//        sqls.forEach(sql->{
//            try (Connection connection = DriverManager.getConnection
//                    ("jdbc:mysql://172.16.116.213:3390/ods", "tidb", "tidb@sunlands");
//                 PreparedStatement preparedStatement = connection.prepareStatement(sql);
//                 ResultSet resultSet = preparedStatement.executeQuery()) {
//                long e = System.currentTimeMillis();
//                System.out.println(Thread.currentThread().getName() + "执行耗时:" + (e - s));
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                //countDownLatch.countDown();
//            }
//        });

        int size=sqls.size();

        CountDownLatch latch=new CountDownLatch(size);



        sqls.forEach(sql->{
            new Thread(){
                @Override
                public void run() {
                    try (Connection connection = DriverManager.getConnection
                            ("jdbc:mysql://172.16.116.213:3390/ods", "tidb", "tidb@sunlands");
                         PreparedStatement preparedStatement = connection.prepareStatement(sql);
                         ResultSet resultSet = preparedStatement.executeQuery()) {
                        long e = System.currentTimeMillis();
                        System.out.println(sql+" "+Thread.currentThread().getName() + "执行耗时:" + (e - s));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                        //countDownLatch.countDown();
                    }
                }
            }.start();
        });

//        for(int i=0;i<sqls.size();i++){
//            String sql=sqls.get(i);
//            new Thread(){
//                @Override
//                public void run() {
//                    try (Connection connection = DriverManager.getConnection
//                            ("jdbc:mysql://172.16.116.213:3390/ods", "tidb", "tidb@sunlands");
//                         PreparedStatement preparedStatement = connection.prepareStatement(sql);
//                         ResultSet resultSet = preparedStatement.executeQuery()) {
//                        long e = System.currentTimeMillis();
//                        System.out.println(sql+" "+Thread.currentThread().getName() + "执行耗时:" + (e - s));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        //countDownLatch.countDown();
//                    }
//                }
//            }.start();
//        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        countDownLatch.countDown();

    }

    public static void main(String[] args) {
        int concurrentNums=10;
        CountDownLatch countDownLatch = new CountDownLatch(concurrentNums);

        ExecutorService executor = Executors.newFixedThreadPool(concurrentNums);
        long s = System.currentTimeMillis();
        System.out.println("获取数据库连接成功，准备执行SQL...");
        try {
            for (int i = 0; i < concurrentNums; i++) {
                TidbConcurrentMutilCase task = new TidbConcurrentMutilCase(countDownLatch);
                executor.execute(task);
            }
            countDownLatch.await();
            executor.shutdownNow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long e = System.currentTimeMillis();
        System.out.println("总耗时：" + (e - s) + "ms");
    }

    public static List<String> getRequestSqls_1(){
        String sql="select count(*) as bizCardCount,sum(bizcard_cash_amount) as bizCardCash from f_mid_business_card where create_time between '2019-11-30 00:00:00' and '2019-12-06 00:00:00' and delete_flag <> 1 and (resp_account in ('huangjunzi') or pm_account in ('huangjunzi'));" +
                "select count(*) as registrationNumberWS,sum(educate_amount) as turnOverWS from f_mid_order_details where payment_time between '2019-11-30 00:00:00' and '2019-12-06 18:01:51' and status_code in ('PAID', 'PRODCHANGED', 'CANCELED', 'FREEZED', 'STUCHANGED','EXPIRED', 'PARTIAL_PAY', 'PAID', 'PRODUCT_CHANGED', 'STU_CHANGED', 'ORD_TERM', 'POSTPONED','FROZEN', 'FINISHED') and delete_flag = 0 and educate_amount is not null and educate_amount<> 0 and ent_package_type<>'special' and (resp_account in ('huangjunzi') or pm_account in ('huangjunzi'));" +
                "select count(*) as oppCount from f_mid_business_card where state_time between '2019-11-30 00:00:00' and '2019-12-06 00:00:00' and delete_flag <> 1 and is_opportunity = '1' and (resp_account in ('huangjunzi') or pm_account in ('huangjunzi'));" +
                "select count(*) as reservationNumber from f_mid_reservation where create_time between '2019-11-30 00:00:00' and '2019-12-06 00:00:00' and delete_flag = 0 and (resp_account in ('huangjunzi') or pm_account in ('huangjunzi'));" ;
        List<String> sqls= Arrays.asList(sql.split(";"));
        return sqls;
    }

    public static List<String> getRequestSqls_2(){
        String sql="select sum(bizcard_present_count_int) as bizCardPresentCount,count(*) as bizCardCount,sum(bizcard_cash_amount) as bizCardCash,sum(bizcard_click_count_int) as bizCardClickCount,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_business_card where create_time between '2019-11-30 00:00:00' and '2019-12-06 00:00:00' and delete_flag <> 1 and (resp_account in (''huangjunzi') or pm_account in ('huangjunzi')) group by project_id,project order by project_id asc;" +
                "select count(*) as assigned,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_business_card where create_time between '2019-11-30 00:00:00' and '2019-12-06 00:00:00' and delete_flag <> 1 and allocate_time <> '0000:00:00 00:00:00' and (resp_account in ('huangjunzi') or pm_account in ('huangjunzi')) group by project_id,project order by project_id asc;" +
                "select sum(educate_amount) as turnOverThisWeekWS,count(*) as registrationNumberThisWeekWS,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_order_details where payment_time between '2019-11-30 00:00:00' and '2019-12-06 00:00:00' and status_code in ('PAID', 'PRODCHANGED', 'CANCELED', 'FREEZED', 'STUCHANGED','EXPIRED', 'PARTIAL_PAY', 'PAID', 'PRODUCT_CHANGED', 'STU_CHANGED', 'ORD_TERM', 'POSTPONED','FROZEN', 'FINISHED') and delete_flag = 0 and educate_amount is not null and educate_amount<> 0 and current_type='1' and ent_package_type <> 'special' and (resp_account in ('wangjing17') or pm_account in ('wangjing17')) group by project_id,project order by project_id asc;" +
                "select count(*) as registrationNumberWS,sum(educate_amount) as turnOverWS,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_order_details where payment_time between '2019-11-30 00:00:00' and '2020-12-06 00:00:00' and status_code in ('PAID', 'PRODCHANGED', 'CANCELED', 'FREEZED', 'STUCHANGED','EXPIRED', 'PARTIAL_PAY', 'PAID', 'PRODUCT_CHANGED', 'STU_CHANGED', 'ORD_TERM', 'POSTPONED','FROZEN', 'FINISHED') and delete_flag = 0 and educate_amount is not null and educate_amount<> 0 and ent_package_type<>'special' and (resp_account in ('wangjing17') or pm_account in ('wangjing17')) group by project_id,project order by project_id asc;" +
                "select count(*) as reservationNumber,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_reservation where create_time between '2019-11-30 00:00:00' and '2020-12-06 00:00:00' and delete_flag = 0 and (resp_account in ('wangjing17') or pm_account in ('wangjing17')) group by project_id,project order by project_id asc;" +
                "select count(*) as oppCount,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_business_card where state_time between '2019-11-30 00:00:00' and '2020-12-06 00:00:00' and delete_flag <> 1 and is_opportunity = '1' and (resp_account in ('wangjing17') or pm_account in ('wangjing17')) group by project_id,project order by project_id asc;";
        List<String> sqls= Arrays.asList(sql.split(";"));
        return sqls;
    }

    public static List<String> getRequestSqls_3(){
        String sql="select sum(cash_amount) as cash,ifnull(advertiser_code, '未知') as advertise_id,ifnull(advertiser_name, '未知') as advertise from f_mid_ad_campain_day where business_date between '2019-11-27' and '2019-11-27' and delete_flag = 0 and flow_studio_id in ('2100998') group by advertise_id,advertise order by advertise_id asc;" +
                "select sum(educate_amount) as turnOverWS,ifnull(advertiser_code, '未知') as advertise_id,ifnull(advertiser_name, '未知') as advertise from f_mid_order_details where payment_time between '2019-11-27 00:00:00' and '2019-11-27 23:59:59' and status_code in ('PAID', 'PRODCHANGED', 'CANCELED', 'FREEZED', 'STUCHANGED','EXPIRED', 'PARTIAL_PAY', 'PAID', 'PRODUCT_CHANGED', 'STU_CHANGED', 'ORD_TERM', 'POSTPONED','FROZEN', 'FINISHED') and delete_flag = 0 and educate_amount is not null and educate_amount<> 0 and ent_package_type<>'special' and flow_studio_id in ('2100998') group by advertise_id,advertise order by advertise_id asc;" +
                "select count(*) as bizCardCount,ifnull(advertiser_code, '未知') as advertise_id,ifnull(advertiser_name, '未知') as advertise from f_mid_business_card where create_time between '2019-11-27 00:00:00' and '2019-11-27 23:59:59' and delete_flag <> 1 and flow_studio_id in ('2100998') group by advertise_id,advertise order by advertise_id asc;";
        List<String> sqls= Arrays.asList(sql.split(";"));
        return sqls;
    }

    public static List<String> getRequestSqls_4(){
        String sql="select sum(bizcard_present_count_int) as bizCardPresentCount,count(*) as bizCardCount,sum(bizcard_cash_amount) as bizCardCash,sum(bizcard_click_count_int) as bizCardClickCount,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_business_card where create_time between '2019-12-17 00:00:00' and '2019-12-17 18:05:53' and delete_flag <> 1 and (resp_account in ('wangjing17') or pm_account in ('wangjing17')) group by project_id,project order by project_id asc;" +
                "select count(*) as assigned,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_business_card where create_time between '2019-12-17 00:00:00' and '2019-12-17 18:05:53' and delete_flag <> 1 and allocate_time <> '0000:00:00 00:00:00' and (resp_account in ('wangjing17') or pm_account in ('wangjing17')) group by project_id,project order by project_id asc;" +
                "select sum(educate_amount) as turnOverThisWeekWS,count(*) as registrationNumberThisWeekWS,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_order_details where payment_time between '2019-12-17 00:00:00' and '2019-12-17 18:05:53' and status_code in ('PAID', 'PRODCHANGED', 'CANCELED', 'FREEZED', 'STUCHANGED','EXPIRED', 'PARTIAL_PAY', 'PAID', 'PRODUCT_CHANGED', 'STU_CHANGED', 'ORD_TERM', 'POSTPONED','FROZEN', 'FINISHED') and delete_flag = 0 and educate_amount is not null and educate_amount<> 0 and current_type='1' and ent_package_type <> 'special' and (resp_account in ('wangjing17') or pm_account in ('wangjing17')) group by project_id,project order by project_id asc;" +
                "select count(*) as registrationNumberWS,sum(educate_amount) as turnOverWS,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_order_details where payment_time between '2019-12-17 00:00:00' and '2019-12-17 18:05:53' and status_code in ('PAID', 'PRODCHANGED', 'CANCELED', 'FREEZED', 'STUCHANGED','EXPIRED', 'PARTIAL_PAY', 'PAID', 'PRODUCT_CHANGED', 'STU_CHANGED', 'ORD_TERM', 'POSTPONED','FROZEN', 'FINISHED') and delete_flag = 0 and educate_amount is not null and educate_amount<> 0 and ent_package_type<>'special' and (resp_account in ('wangjing17') or pm_account in ('wangjing17')) group by project_id,project order by project_id asc;" +
                "select count(*) as reservationNumber,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_reservation where create_time between '2019-12-17 00:00:00' and '2019-12-17 18:05:53' and delete_flag = 0 and (resp_account in ('wangjing17') or pm_account in ('wangjing17')) group by project_id,project order by project_id asc;" +
                "select count(*) as oppCount,ifnull(site_project_code, '未知') as project_id,ifnull(site_project_name, '未知') as project from f_mid_business_card where state_time between '2019-12-17 00:00:00' and '2019-12-17 18:05:53' and delete_flag <> 1 and is_opportunity = '1' and (resp_account in ('wangjing17') or pm_account in ('wangjing17')) group by project_id,project order by project_id asc;";
        List<String> sqls= Arrays.asList(sql.split(";"));
        return sqls;
    }

    public static List<String> getRequestSqls_5(){
        String sql="select count(*) as bizCardCount,sum(bizcard_cash_amount) as bizCardCash from f_mid_business_card where create_time between '2019-12-16 00:00:00' and '2019-12-16 23:59:59' and delete_flag <> 1;" +
                "select count(*) as registrationNumberWS,sum(educate_amount) as turnOverWS from f_mid_order_details where payment_time between '2019-12-16 00:00:00' and '2019-12-16 23:59:59' and status_code in ('PAID', 'PRODCHANGED', 'CANCELED', 'FREEZED', 'STUCHANGED','EXPIRED', 'PARTIAL_PAY', 'PAID', 'PRODUCT_CHANGED', 'STU_CHANGED', 'ORD_TERM', 'POSTPONED','FROZEN', 'FINISHED') and delete_flag = 0 and educate_amount is not null and educate_amount<> 0 and ent_package_type<>'special';" +
                "select count(*) as oppCount from f_mid_business_card where state_time between '2019-12-16 00:00:00' and '2019-12-16 23:59:59' and delete_flag <> 1 and is_opportunity = '1';" +
                "select count(*) as reservationNumber from f_mid_reservation where create_time between '2019-12-16 00:00:00' and '2019-12-16 23:59:59' and delete_flag = 0;";
        List<String> sqls= Arrays.asList(sql.split(";"));
        return sqls;
    }

    public static List<String> getRequestSqls_6(){
        String sql="select sum(bizcard_present_count_int) as bizCardPresentCount,count(*) as bizCardCount,sum(bizcard_cash_amount) as bizCardCash,sum(bizcard_click_count_int) as bizCardClickCount,ifnull(resp_account, '未知') as respAccount_id,ifnull(resp_account, '未知') as respAccount from f_mid_business_card where create_time between '2019-11-21 00:00:00' and '2019-11-21 18:14:28' and delete_flag <> 1 and (resp_account in ('lishaohua') or pm_account in ('lishaohua')) group by respAccount_id,respAccount order by respAccount_id asc;" +
                "select count(*) as assigned,ifnull(resp_account, '未知') as respAccount_id,ifnull(resp_account, '未知') as respAccount from f_mid_business_card where create_time between '2019-11-21 00:00:00' and '2019-11-21 18:14:28' and delete_flag <> 1 and allocate_time <> '0000:00:00 00:00:00' and (resp_account in ('lishaohua') or pm_account in ('lishaohua')) group by respAccount_id,respAccount order by respAccount_id asc;" +
                "select sum(educate_amount) as turnOverThisWeekWS,count(*) as registrationNumberThisWeekWS,ifnull(resp_account, '未知') as respAccount_id,ifnull(resp_account, '未知') as respAccount from f_mid_order_details where payment_time between '2019-11-21 00:00:00' and '2019-11-21 18:14:28' and status_code in ('PAID', 'PRODCHANGED', 'CANCELED', 'FREEZED', 'STUCHANGED','EXPIRED', 'PARTIAL_PAY', 'PAID', 'PRODUCT_CHANGED', 'STU_CHANGED', 'ORD_TERM', 'POSTPONED','FROZEN', 'FINISHED') and delete_flag = 0 and educate_amount is not null and educate_amount<> 0 and current_type='1' and ent_package_type <> 'special' and (resp_account in ('lishaohua') or pm_account in ('lishaohua')) group by respAccount_id,respAccount order by respAccount_id asc;" +
                "select count(*) as registrationNumberWS,sum(educate_amount) as turnOverWS,ifnull(resp_account, '未知') as respAccount_id,ifnull(resp_account, '未知') as respAccount from f_mid_order_details where payment_time between '2019-11-21 00:00:00' and '2019-11-21 18:14:28' and status_code in ('PAID', 'PRODCHANGED', 'CANCELED', 'FREEZED', 'STUCHANGED','EXPIRED', 'PARTIAL_PAY', 'PAID', 'PRODUCT_CHANGED', 'STU_CHANGED', 'ORD_TERM', 'POSTPONED','FROZEN', 'FINISHED') and delete_flag = 0 and educate_amount is not null and educate_amount<> 0 and ent_package_type<>'special' and (resp_account in ('lishaohua') or pm_account in ('lishaohua')) group by respAccount_id,respAccount order by respAccount_id asc;" +
                "select count(*) as reservationNumber,ifnull(resp_account, '未知') as respAccount_id,ifnull(resp_account, '未知') as respAccount from f_mid_reservation where create_time between '2019-11-21 00:00:00' and '2019-11-21 18:14:28' and delete_flag = 0 and (resp_account in ('lishaohua') or pm_account in ('lishaohua')) group by respAccount_id,respAccount order by respAccount_id asc;" +
                "select count(*) as oppCount,ifnull(resp_account, '未知') as respAccount_id,ifnull(resp_account, '未知') as respAccount from f_mid_business_card where state_time between '2019-11-21 00:00:00' and '2019-11-21 18:14:28' and delete_flag <> 1 and is_opportunity = '1' and (resp_account in ('lishaohua') or pm_account in ('lishaohua')) group by respAccount_id,respAccount order by respAccount_id asc;";
        List<String> sqls= Arrays.asList(sql.split(";"));
        return sqls;
    }

}
