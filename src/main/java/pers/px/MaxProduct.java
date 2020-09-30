package pers.px;

public class MaxProduct {
    public static void main(String[] args) {

    }

    public int maxProduct(int[] nums) {
        int length=nums.length;
        int[] maxF = new int[length];
        int[] minF = new int[length];
        System.arraycopy(nums, 0, maxF, 0, length);
        System.arraycopy(nums, 0, minF, 0, length);

        int max = nums[0];
        for (int i = 1; i < nums.length; i++) {
            maxF[i]=Math.max(maxF[i - 1] * nums[i], Math.max(nums[i], minF[i - 1] * nums[i]));
            minF[i]=Math.min(minF[i - 1] * nums[i], Math.min(nums[i], maxF[i - 1] * nums[i]));
        }
        return max;
    }
}
