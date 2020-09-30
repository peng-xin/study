package pers.px;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.Iterator;

public class CommonCsv {
    public static void main(String[] args) {
        read("C:\\Users\\stphen\\AppData\\Local\\kingsoft\\WPS Cloud Files\\userdata\\qing\\filecache\\一世无欺的云文档\\应用\\雷达备份\\magical-desktop-tv7fnek\\我的桌面\\t_wide_table.csv");
        read("C:\\Users\\stphen\\AppData\\Local\\kingsoft\\WPS Cloud Files\\userdata\\qing\\filecache\\一世无欺的云文档\\应用\\雷达备份\\magical-desktop-tv7fnek\\我的桌面\\t_wide_table1.csv");
        read("C:\\Users\\stphen\\AppData\\Local\\kingsoft\\WPS Cloud Files\\userdata\\qing\\filecache\\一世无欺的云文档\\应用\\雷达备份\\magical-desktop-tv7fnek\\我的桌面\\t_wide_table2.csv");
    }

    private static void read(String filePath) {
        Reader in = null;
        Iterable<CSVRecord> records = null;
        try {
            in = new InputStreamReader(new FileInputStream(filePath));
            records = CSVFormat.EXCEL.parse(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (CSVRecord record : records) {
            for (Iterator<String> iterator = record.iterator(); iterator.hasNext(); ) {
                String s = iterator.next();
                System.out.print(s + "-->");
            }
            System.out.println(record.getRecordNumber());
            System.out.println(record.size());
        }
    }
}
