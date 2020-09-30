package pers.px;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Pattern ST_PATTEN = Pattern.compile("([\\w\\u4e00-\\u9fa5]+)\\.([\\w\\u4e00-\\u9fa5]+)");
        final Matcher matcher = ST_PATTEN.matcher("excel.订单明细1");
        System.out.println(matcher.matches());

        try (Connection connection = DriverManager.getConnection
                ("jdbc:mysql://172.16.117.177:4000/tpcc", "tidb", "tidb@sunlands");
//        ("jdbc:mysql://172.16.117.96:3306/hive", "hive", "hive");
        ) {
            connection.setAutoCommit(false);
            long s = System.currentTimeMillis();
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into data_warehouse.test(a) values('2'),('2.2'),('222222222222.222222')");
//            ResultSet resultSet = statement.executeQuery("select  replace('快手|1949445018|口播2-素造-0731-2\t\t\t',char(9),'')");
            connection.commit();
            ResultSet resultSet = statement.executeQuery("select orders.account_name,count(*),sum(reser.reservation_price) from f_mid_order_details as orders left join f_mid_reservation as reser on orders.site_id=reser.site_id where orders.payment_date>'2020-08-01' group by orders.account_name");
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            System.out.println(resultSetMetaData.getColumnType(1));
            System.out.println(resultSetMetaData.getColumnTypeName(1));
            System.out.println(resultSetMetaData.getColumnLabel(1));
            System.out.println(resultSetMetaData.getColumnClassName(1));
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
            long e = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + "执行耗时:" + (e - s));

        } catch (Exception e) {
            e.printStackTrace();
        }

        int begin = 737060;
//        DateTime dateTime=DateTime.parse("2018-01-01");
//        while (dateTime.compareTo(DateTime.now())<0){
//            System.out.println("partition p"+begin+" values less than (to_days('"+dateTime.toString("YYYY-MM-dd")+"'))");
//            begin+=7;
//            dateTime=dateTime.plusDays(7);
//        }

        System.out.println(DateTime.parse("2020-1-1"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            System.out.println(simpleDateFormat.parse("2020-01-1"));
            System.out.println(simpleDateFormat.parse("2020-1-1"));
            System.out.println(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2020-1-1"));
            System.out.println(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime("2020-01-1"));
            System.out.println(DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime("2020/01/1"));
            System.out.println(DateTimeFormat.forPattern("MM/dd/yyyy").parseDateTime("01/1/2020"));
            System.out.println(DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(null));
            System.out.println(DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(""));
            System.out.println(DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime("12"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateTime.parse("2020-1-1", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        ConvertToTitle.convertToTitle(26);
        ConvertToTitle.convertToTitle(701);


        System.out.println(ConvertToTitle.convertToTitle(701));
        System.out.println(ConvertToTitle.convertToTitle(100));
        System.out.println(ConvertToTitle.convertToTitle(27));
        System.out.println(ConvertToTitle.convertToTitle(28));

        System.out.println(convertSheetHead(701));
        System.out.println(convertSheetHead(100));
        System.out.println(convertSheetHead(27));
        System.out.println(convertSheetHead(28));

//        System.out.println("Hello World!");
//        AvlTree avlTree = new AvlTree();
//        avlTree.insert(1);
//        avlTree.printTree();
//        avlTree.insert(5);
//        avlTree.insert(3);
//        avlTree.printTree();
//        avlTree.printBalance();
//        System.out.println(Kmp.kmpSearch("asadsdsdssasasa","asaddaasa"));
//        System.out.println(Kmp.kmpSearch("asadsdsdsdssasasa","dsdsdsdsAds"));
//        System.out.println(divBy3("12311311215"));
//        System.out.println(maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -1, 5, -5, 6}));
//        DPCoins.coins(16,new int[]{1,2,5});
//        DPCoins.cutStell(20,new int[]{0,1,5,8,9,10,17,17,20,24,30});

        int[] source = new int[]{1, 2, 3, 5, 7, 9, 10, 12, 13};
        int a = 2;
        source[a] = source[a++];

        System.out.println(twoSum(new int[]{1, 2, 3, 5, 7, 9, 10, 12, 13}, 12));
    }

    private static String convertSheetHead(int colIndex) {
        StringBuilder result = new StringBuilder();
        while (colIndex > 0) {
            if (result.length() > 0) {
                result.append((char) (65 + colIndex % 26 - 1));
            } else {
                result.append((char) (65 + colIndex % 26));
            }
            colIndex = colIndex / 26;
        }
        return result.reverse().toString();
    }

    public static int[] next(String nextString) {
        int length = nextString.length();
        int[] result = new int[length];
        int index = 0, next = -1;
        result[index] = next;
        while (index < length) {
            if (next == -1 || nextString.charAt(index) == nextString.charAt(next)) {
                index++;
                next++;
                if (result[index] != result[next]) {
                    result[index] = next;
                } else {
                    result[index] = result[next];
                }
            } else {
                next = result[next];
            }
        }
        return result;
    }

    private static int divBy3(String num) {
        if (num == null) {
            return 0;
        }
        int last = 0, cur = 0, sum = 0, total = 0;
        for (int i = 0; i < num.length(); i++) {
            cur = Integer.valueOf(num.charAt(i));
            if (cur % 3 == 0) {
                total += 1;
                sum = 0;
            } else {
                for (int j = i - 1; j > last; j--) {
                    sum += cur % 3;
                    if (sum % 3 == 0) {
                        total += 1;
                        sum = 0;
                        last = i;
                    }
                }
            }
        }
        return total;
    }

//    public static int maxSubArray(int[] nums) {
//        int[] dp = new int[nums.length];
//        dp[0] = nums[0];
//        int max = nums[0];
//        for (int i = 1; i < nums.length; i++) {
//            dp[i] = Math.max(dp[i- 1] + nums[i], nums[i]);
//            if (max < dp[i]) {
//                max = dp[i];
//            }
//        }
//        return max;
//    }

    private static int maxSubArray(int[] nums) {
        int n = nums.length, maxSum = nums[0];
        for (int i = 1; i < n; ++i) {
            if (nums[i - 1] > 0) nums[i] += nums[i - 1];
            maxSum = Math.max(nums[i], maxSum);
        }
        return maxSum;
    }

    private static List<int[]> twoSum(int[] source, int target) {
        List<int[]> result = new ArrayList<>();
        Map map = new HashMap();
        for (int i = 0; i < source.length; i++) {
            if (map.get(target - source[i]) != null) {
                result.add(new int[]{target - source[i], source[i]});
            }
            map.put(source[i], i);
        }
        result.forEach(arr -> System.out.println(Arrays.toString(arr)));
        return result;
    }
}