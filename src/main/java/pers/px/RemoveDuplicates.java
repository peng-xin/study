package pers.px;

public class RemoveDuplicates {
    public String removeDuplicates(String S) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < S.length(); i++) {
            if (result.length() > 0) {
                if (result.charAt(result.length() - 1) != S.charAt(i)) {
                    result.append(S.charAt(i));
                } else {
                    result.deleteCharAt(result.length() - 1);
                }
            } else {
                result.append(S.charAt(i));
            }
        }
        return result.toString();
    }
}
