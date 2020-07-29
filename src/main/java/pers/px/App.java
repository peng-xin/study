package pers.px;

import java.util.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        ConvertToTitle.convertToTitle(26);
        ConvertToTitle.convertToTitle(701);

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
            if(result.length()>0){
                result.append((char) (65 + colIndex % 26-1));
            }else {
                result.append((char) (65 + colIndex % 26));
            }
            colIndex = colIndex / 26;
        }
        return result.toString();
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