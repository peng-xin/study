package pers.px;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpiralOrder {

    public static List<Integer> solution(int[][] matrix) {
        if (matrix == null || matrix.length < 1) {
            return Collections.emptyList();
        }

        int rowSize = matrix.length;
        int colSize = matrix[0].length;

        int top = 0, right = colSize - 1, bottom = rowSize - 1, left = 0, index = 0;

        List<Integer> result = new ArrayList(rowSize * colSize);
        while (true) {
            for (index = left; index <= right; index++) {
                result.add(matrix[top][index]);
            }
            if (++top > bottom) {
                break;
            }
            for (index = top; index <= bottom; index++) {
                result.add(matrix[index][right]);
            }
            if (--right < left) {
                break;
            }
            for (index = right; index >= left; index--) {
                result.add(matrix[bottom][index]);
            }
            if (--bottom < top) {
                break;
            }
            for (index = bottom; index >= top; index--) {
                result.add(matrix[index][left]);
            }
            if (++left > right) {
                break;
            }
        }
        return result;
    }
}
