package pers.px;

public class GenerateMatrix {
    public static int[][] generateMatrix(int n) {
        int[][] result = new int[n][n];
        int top = 0, right = n - 1, bottom = n - 1, left = 0, offset = 0, index = 0;

        while (offset <= n * n) {
            for (index = left; index <= right; index++) {
                result[top][index] = ++offset;
            }
            if (++top > bottom) {
                break;
            }
            for (index = top; index <= bottom; index++) {
                result[index][right] = ++offset;
            }
            if (--right < left) {
                break;
            }
            for (index = right; index >= left; index--) {
                result[bottom][index] = ++offset;
            }
            if (--bottom < top) {
                break;
            }
            for (index = bottom; index >= top; index--) {
                result[index][left] = ++offset;
            }
            if (++left > right) {
                break;
            }
        }
        return result;
    }
}
