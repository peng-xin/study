package pers.px;

import java.util.Arrays;

public class LengthOfLIS {
    public int lengthOfLIS(int[] nums) {
        if (nums == null || nums.length < 1) {
            return 0;
        }
        int[] dp = new int[nums.length];
        dp[0] = 1;
        int max = dp[0];
        for (int i = 1; i < nums.length; i++) {
            int tmp=1;
            for (int j = i - 1; j >= 0; j--) {
                if (nums[i] > nums[j]) {
                    tmp = Math.max(tmp, dp[j] + 1);
                }
            }
            dp[i] = tmp;
            max=Math.max(max,tmp);
            System.out.println(i + "==>" + Arrays.toString(dp));
        }
        return max;
    }
}
