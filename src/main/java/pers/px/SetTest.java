package pers.px;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class SetTest {
    public static void main(String[] args) {
        sort();
    }

    private static void sort(){
        Set set = new HashSet();
        set.add(20180101);
        set.add(20180103);
        set.add(20180104);
        set.add(20180102);
        set.add(20180105);


        System.out.println(set.toString());
        System.out.println(set.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()));
    }
}
