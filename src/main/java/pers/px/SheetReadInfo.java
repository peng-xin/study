package pers.px;

import lombok.Data;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class SheetReadInfo {
    String name;
    Integer colStart = 1;
    Integer rowStart = 1;
    Integer headNumber = 1;
    LinkedHashMap<String, String> schema;
    List<String[]> data;

    @Override
    public String toString() {
        return "SheetReadInfo{" +
                "name='" + name + '\'' +
                ", colStart=" + colStart +
                ", rowStart=" + rowStart +
                ", headNumber=" + headNumber +
                ", schema=" + schema +
                ", data=" + (data == null ? null : Arrays.toString(data.stream().map(row -> System.lineSeparator().concat(String.join(",", row))).toArray())) +
                '}';
    }
}
