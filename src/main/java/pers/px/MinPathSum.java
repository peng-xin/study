package pers.px;

public class MinPathSum {
    public int minPathSum(int[][] grid) {
        if (grid == null || grid[0] == null || grid[0].length < 1) {
            return 0;
        }
        int rowSize = grid.length;
        int colSize = grid[0].length;
        int[][] dp = new int[rowSize][colSize];

        dp[0][0] = grid[0][0];

        for (int i = 1; i < grid[0].length; i++) {
            dp[0][i] = grid[0][i] + dp[0][i - 1];
        }
        for (int i = 1; i < grid[0].length; i++) {
            dp[i][0] = grid[i][0] + dp[i - 1][0];
        }

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (i == 0) {

                }
            }
            dp[i][0] = grid[i][0];
        }


        return dp[rowSize - 1][colSize - 1];
    }
}
