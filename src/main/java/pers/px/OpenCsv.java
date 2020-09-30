package pers.px;

import au.com.bytecode.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class OpenCsv {
    public static void main(String[] args) {
        read("C:\\Users\\stphen\\AppData\\Local\\kingsoft\\WPS Cloud Files\\userdata\\qing\\filecache\\一世无欺的云文档\\应用\\雷达备份\\magical-desktop-tv7fnek\\我的桌面\\t_wide_table.csv");
        read("C:\\Users\\stphen\\AppData\\Local\\kingsoft\\WPS Cloud Files\\userdata\\qing\\filecache\\一世无欺的云文档\\应用\\雷达备份\\magical-desktop-tv7fnek\\我的桌面\\t_wide_table1.csv");
        read("C:\\Users\\stphen\\AppData\\Local\\kingsoft\\WPS Cloud Files\\userdata\\qing\\filecache\\一世无欺的云文档\\应用\\雷达备份\\magical-desktop-tv7fnek\\我的桌面\\t_wide_table2.csv");
    }

    private static void read(String filePath) {
        try (CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)),"gb2312")))) {
            String[] data;
            while ((data=csvReader.readNext())!=null){
                System.out.println(Arrays.toString(data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
