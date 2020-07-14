package pers.px;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    public static void main(String[] args) {
        String fileName = args[0];
        readExcel(fileName);
//        readExcel("C:\\Users\\stphen\\Documents\\WeChat Files\\wxid_9899908997612\\FileStorage\\File\\2020-06\\腾讯云资源服务器-数据中心(1)(1).xlsx", sheetReadInfoList);
    }

    public static Map<String, Object> readExcel(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            LOGGER.error("fileName must have a non null value");
            return null;
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);

        switch (fileExtension) {
            case "xls":
                readExcel(fileName, ExcelTypeEnum.XLS);
                break;
            case "xlsx":
                readExcel(fileName, ExcelTypeEnum.XLSX);
                break;
            case "csv":
//                readCsv(fileName);
                break;
            default:
                LOGGER.error("{} must be a doc/docx/csv file",fileName);
                break;
        }
        return null;
    }

    private static Map<String, Object> readExcel(String fileName, ExcelTypeEnum excelTypeEnum) {
        ExcelReader excelReader = EasyExcel.read(fileName)
                .excelType(excelTypeEnum)
//                .readCacheSelector(new SimpleReadCacheSelector(5, 20))
                .build();

        List<ReadSheet> sheetList = excelReader.excelExecutor().sheetList();

        for (ReadSheet readSheet : sheetList) {
            NoModelDataListener noModelDataListener = new NoModelDataListener();
            readSheet.setCustomReadListenerList(Collections.singletonList(noModelDataListener));
            LOGGER.info("sheet name is {}", readSheet.getSheetName());
            LOGGER.info("sheet No is {}", readSheet.getSheetNo());

//            LOGGER.info("sheet head is {}", Arrays.toString(readSheet.getHead().toArray()));
            excelReader.read(readSheet);
//            LOGGER.info(sheetReadInfoList.get(index).toString());
        }

        excelReader.finish();
        return null;
    }
}
