package pers.px;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TidbConcurrentFix implements Callable {
    private CountDownLatch countDownLatch = null;
    private static Map.Entry<String, String> sqlInfo = null;
    private static Map.Entry<String, String[]> jdbcParams = null;
    private static Map<String, String[]> jdbcParamsMap = new LinkedHashMap<>();
    private static Map<String, String> sqlMap = new LinkedHashMap(18);
    private static Map<String, Map<String, Map<String, Map<String, String>>>> execResultByCluster = new LinkedHashMap(2);
    private static Map<String, Map<String, Map<String, String>>> execResultMap = new LinkedHashMap(18);
    private static List<Integer> concurrentNumList = Arrays.asList(new Integer[]{1, 5, 10, 20, 50});

    TidbConcurrentFix(Map.Entry<String, String> sqlInfo, Map.Entry<String, String[]> jdbcParams, CountDownLatch countDownLatch) {
        this.sqlInfo = sqlInfo;
        this.jdbcParams = jdbcParams;
        this.countDownLatch = countDownLatch;
    }

    static {
        jdbcParamsMap.put("219cluster", new String[]{"jdbc:mysql://49.233.184.219:3390/tpcc", "tidb", "tidb@sunlands"});
//        jdbcParamsMap.put("213cluster", new String[]{"jdbc:mysql://172.16.117.177:4000/tpcc", "tidb", "tidb@sunlands"});

        /*sql1*/
        sqlMap.put("1天(0820),null,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date = '2019-08-20' group by channel");
        sqlMap.put("4天(0820-0823),null,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-08-20' and business_date<='2019-08-23' group by channel");
        sqlMap.put("1周(0820-0826),null,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-08-20' and business_date<='2019-08-26'  group by channel");
        sqlMap.put("半个月(0820-0903),null,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-08-20' and business_date<='2019-09-03'  group by channel");
        sqlMap.put("一个月(0820-0920),null,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-08-20' and business_date<='2019-09-20'  group by channel");
        sqlMap.put("一个季度(0701-1001),null,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-07-01' and business_date<='2019-10-01'  group by channel");
        sqlMap.put("半年(0401-1001),null,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-04-01' and business_date<='2019-10-01'  group by channel");
        /*sql2*/
        sqlMap.put("1天(0814),account_name-channel,null", "select /*count(distinct(keyword_id)),*/ sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '360网盟' and account_name = '尚德机构196' and business_date = '2019-08-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        sqlMap.put("1天(0814),account_name-channel,null", "select /*count(distinct(keyword_id)),*/ sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '360网盟' and account_name = '尚德机构196' and business_date = '2019-08-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        sqlMap.put("1天(0814),account_name-channel,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '360网盟' and account_name = '尚德机构196' and business_date = '2019-08-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        sqlMap.put("1天(0814),account_name-channel,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '360网盟' and account_name = '尚德机构196' and business_date = '2019-08-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        sqlMap.put("一个月(0814-0914),account_name-channel,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '360网盟' and account_name = '尚德机构196' and business_date between '2019-08-14' and '2019-09-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        sqlMap.put("一个月(0814-0914),account_name-channel,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '360网盟' and account_name = '尚德机构196' and business_date between '2019-08-14' and '2019-09-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        /*sql3*/
        sqlMap.put("1天(0829),account_name-channel,null", "select /*count(distinct(keyword_id)),*/ sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date = '2019-08-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        sqlMap.put("1天(0829),account_name-channel,null", "select /*count(distinct(keyword_id)),*/ sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date = '2019-08-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        sqlMap.put("1天(0829),account_name-channel,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date = '2019-08-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        sqlMap.put("1天(0829),account_name-channel,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date = '2019-08-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        sqlMap.put("一个月(0829-0929),account_name-channel,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date between '2019-08-29' and '2019-09-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
        sqlMap.put("一个月(0829-0929),account_name-channel,keyword_id", "select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date between '2019-08-29' and '2019-09-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name");
    }

    public TidbConcurrentFix(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

//    public void run() {
//        System.out.println(Thread.currentThread().getName() + "准备执行");
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date = '2019-08-20' group by channel;";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-08-20' and business_date<='2019-08-23' group by channel;";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-08-20' and business_date<='2019-08-26'  group by channel;";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-08-20' and business_date<='2019-09-03'  group by channel;";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-08-20' and business_date<='2019-09-20'  group by channel;";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-07-01' and business_date<='2019-10-01'  group by channel;";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel from f_fact_ad_keyword_range where business_date >= '2019-04-01' and business_date<='2019-10-01'  group by channel;";
////        String sql="select /*count(distinct(keyword_id)),*/ sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '360网盟' and account_name = '尚德机构196' and business_date = '2019-08-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select /*count(distinct(keyword_id)),*/ sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date = '2019-08-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select /*count(distinct(keyword_id)),*/ sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '360网盟' and account_name = '尚德机构196' and business_date = '2019-08-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select /*count(distinct(keyword_id)),*/ sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date = '2019-08-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '360网盟' and account_name = '尚德机构196' and business_date = '2019-08-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date = '2019-08-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '360网盟' and account_name = '尚德机构196' and business_date = '2019-08-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date = '2019-08-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '360网盟' and account_name = '尚德机构196' and business_date between '2019-08-14' and '2019-09-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range use index(account_name) where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date between '2019-08-29' and '2019-09-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '360网盟' and account_name = '尚德机构196' and business_date between '2019-08-14' and '2019-09-14' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
////        String sql="select count(distinct(keyword_id)), sum(click_count), sum(cash_amount), sum(chat_count), channel, account_name, site_name, campaign_name, ad_group_name, keyword_name from f_fact_ad_keyword_range where channel = '腾讯广点通' and account_name = '3859903' and (resp_account = 'chenzipeng' or pm_account = 'tangyifan') and business_date between '2019-08-29' and '2019-09-29' group by channel, account_name, site_name, campaign_name, ad_group_name, keyword_name";
//
//        try (Connection connection = DriverManager.getConnection
////                ("jdbc:mysql://49.233.184.219:4000/tpcc", "tidb", "tidb@sunlands");
//        ("jdbc:mysql://172.16.117.177:4000/tpcc", "tidb", "tidb@sunlands");
//        ) {
//            long s = System.currentTimeMillis();
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            long e = System.currentTimeMillis();
//            System.out.println(Thread.currentThread().getName() + "执行耗时:" + (e - s));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            countDownLatch.countDown();
//        }
//    }

    public static void main(String[] args) {
        concurrentTestStart();
    }

    @Override
    public Object call() throws Exception {
        Long execTime = 0L;
        System.out.println(Thread.currentThread().getName() + "准备执行");
        try (Connection connection = DriverManager.getConnection
                (jdbcParams.getValue()[0], jdbcParams.getValue()[1], jdbcParams.getValue()[2]);
        ) {
            long s = System.currentTimeMillis();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlInfo.getValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            long e = System.currentTimeMillis();
            execTime = e - s;
            System.out.println(Thread.currentThread().getName() + "执行耗时:" + execTime);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
        return execTime;
    }

    private static void concurrentTestStart() {
        for (Map.Entry<String, String[]> entry : jdbcParamsMap.entrySet()) {
            iterateBySql(entry);
            execResultByCluster.put(entry.getKey(), execResultMap);
            execResultMap = new LinkedHashMap(18);
        }
        map2csvRow();
    }

    private static void iterateBySql(Map.Entry<String, String[]> jdbcParams) {
        for (Map.Entry entry : sqlMap.entrySet()) {
            Map execResult;
            List<Future> futureList;
            List<Long> resultList;
            iterateByConcurrentNum(jdbcParams, entry);
        }
    }

    private static void iterateByConcurrentNum(Map.Entry<String, String[]> jdbcParams, Map.Entry<String, String> sqlInfo) {
        Map<String, Map<String, String>> concurrentMap = new LinkedHashMap(concurrentNumList.size());
        for (Integer concurrentNum : concurrentNumList) {
            CountDownLatch countDownLatch = new CountDownLatch(concurrentNum);
            ExecutorService executor = Executors.newFixedThreadPool(concurrentNum);
            Map<String, String> execResult = new HashMap(3, 1);
            List<Future> futureList = new ArrayList<>(concurrentNum);
            List<Long> resultList = null;
            long s = System.currentTimeMillis();
            System.out.println("获取数据库连接成功，准备执行SQL...");
            try {
                for (int i = 0; i < concurrentNum; i++) {
                    TidbConcurrentFix task = new TidbConcurrentFix(sqlInfo, jdbcParams, countDownLatch);
                    Future future = executor.submit(task);
                    futureList.add(future);
                }
                countDownLatch.await();
                executor.shutdownNow();
            } catch (InterruptedException e) {
                System.out.println(MessageFormat.format("exec error. message is {0}", e.getMessage()));
            }
            resultList = collectFutureValue(futureList);
            Collections.sort(resultList);
            long e = System.currentTimeMillis();
            execResult.put("线程数", String.valueOf(concurrentNum));
            execResult.put("线程执行情况", String.join(",", resultList.stream().map(String::valueOf).collect(Collectors.joining(","))));
            execResult.put("最短执行时间", String.valueOf(resultList.get(0)));
            execResult.put("最长执行时间", String.valueOf(resultList.get(resultList.size() - 1)));
            execResult.put("本测试总用时", String.valueOf(e - s));
            concurrentMap.put(String.valueOf(concurrentNum), execResult);
            System.out.println("总耗时：" + (e - s) + "ms");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException exception) {
                System.out.println(MessageFormat.format("sleep error. message is {0}", exception.getMessage()));
            }
        }
        execResultMap.put(sqlInfo.getKey(), concurrentMap);
    }

    private static List<Long> collectFutureValue(List<Future> futureList) {
        List<Long> resultList = new ArrayList<>(futureList.size());
        for (Future future : futureList) {
            long execTime = 0;
            try {
                execTime = ((Long) future.get());
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(MessageFormat.format("future get error. message is {0}", e.getMessage()));
            }
            resultList.add(execTime);
        }
        return resultList;
    }

    private static void map2csv() {
        System.out.println(MessageFormat.format("\"{0}\",\"{1}\",\"{2}\",\"{3}\",\"{4}\",\"{5}\",\"{6}\",\"{7}\"", "集群名字", "时间跨度", "索引", "distinct", "线程数", "最短执行时间", "最长执行时间", "各线程执行详情"));
        execResultByCluster.forEach((clusterName, execResultMap) -> {
            System.out.println(clusterName);
            execResultMap.forEach((desc, detail) -> {
                detail.forEach((concurrentNum, concurrentDetail) -> {
                    String[] descArr = desc.split(",");
                    System.out.println(MessageFormat.format("\"{0}\",\"{1}\",\"{2}\",\"{3}\",\"{4}\",\"{5}\",\"{6}\",\"{7}\"", "", descArr[0], String.join(",", descArr[1].split("-")), String.join(",", Arrays.copyOf(descArr[2].split("_"), descArr[2].split("_").length - 1)), concurrentDetail.get("线程数"), concurrentDetail.get("最短执行时间"), concurrentDetail.get("最长执行时间"), concurrentDetail.get("线程执行情况")));
                });
            });
            System.out.println();
            System.out.println();
            System.out.println();
        });
    }

    private static void map2csvRow() {
        System.out.print(MessageFormat.format("\"{0}\",\"{1}\",\"{2}\",\"{3}\"", "集群名字", "时间跨度", "索引", "distinct"));
        concurrentNumList.forEach(concurrentNum -> {
            System.out.print(MessageFormat.format(",\"{0}并发\"", concurrentNum));
        });
        System.out.println();

        execResultByCluster.forEach((clusterName, execResultMap) -> {
            System.out.println(clusterName);
            execResultMap.forEach((desc, detail) -> {
                String[] descArr = desc.split(",");
                System.out.print(MessageFormat.format("\"{0}\",\"{1}\",\"{2}\",\"{3}\"", "", descArr[0], String.join(",", descArr[1].split("-")), String.join(",", Arrays.copyOf(descArr[2].split("_"), descArr[2].split("_").length - 1))));
                detail.forEach((concurrentNum, concurrentDetail) -> {
                    System.out.print(MessageFormat.format(",\"{0}~{1}\"", concurrentDetail.get("最短执行时间"), concurrentDetail.get("最长执行时间")));
                });
                System.out.println();
            });
            System.out.println();
            System.out.println();
            System.out.println();
        });
    }
}
