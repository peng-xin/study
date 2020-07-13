package pers.px;

public class ClimbStairs {
    public int climbStairs(int n) {
        if(n==1){
            return 1;
        }
        int[] dp=new int[2];
        dp[0]=1;
        dp[1]=2;
        for(int i=2;i<n;i++){
            dp[1]+=dp[0];
            dp[0]=dp[1]-dp[0];
        }
        return dp[1];
    }
}
