package pers.px;

import java.util.List;

public class MinimumTotal {
    public int minimumTotal(List<List<Integer>> triangle) {
        if (triangle == null || triangle.get(0) == null || triangle.get(0).size() < 1) {
            return 0;
        }
        int[] dp = new int[triangle.size() + 1];
        for (int i = triangle.size() - 1; i >= 0; i--) {
//            upDate(dp, triangle.get(i));
            List<Integer> list = triangle.get(i);
            for (int j = 0; j < list.size(); j++) {
                dp[j] = list.get(j) + Math.min(dp[j], dp[j + 1]);
            }
        }
        return dp[0];
    }

//    private void upDate(int[] dp, List<Integer> row) {
//        int len = row.size();
//        for (int j = 0; j < len; j++) {
//            dp[j] = row.get(j) + Math.min(dp[j], dp[j + 1]);
//        }
//    }
}
