package pers.px;

import java.util.Arrays;
import java.util.List;

public class MinimumTotal {
    public int minimumTotal(List<List<Integer>> triangle) {
        if (triangle == null || triangle.get(0) == null || triangle.get(0).size() < 1) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        int[] dp = new int[triangle.size()];
        List<Integer> last = triangle.get(triangle.size() - 1);
        for (int i = 0; i < last.size(); i++) {
            dp[i] = last.get(i);
        }
        for (int i = triangle.size() - 2; i >= 0; i++) {
            List<Integer> list = triangle.get(i);
            for (int j = i; j >= 0; j--) {
                dp[j] = list.get(j) + Math.min(dp[j], dp[j + 1]);
                min = Math.min(dp[j], dp[j + 1]);
            }
            System.out.println(Arrays.toString(dp));
        }
        return min;
    }
}
