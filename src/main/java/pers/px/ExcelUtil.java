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

        List<SheetReadInfo> sheetReadInfoList = new ArrayList<>();
        SheetReadInfo sheetReadInfo1 = new SheetReadInfo();
        sheetReadInfo1.setRowStart(5);
        sheetReadInfo1.setColStart(3);
        sheetReadInfo1.setHeadNumber(sheetReadInfo1.getRowStart() + sheetReadInfo1.getHeadNumber() - 1);
        sheetReadInfoList.add(sheetReadInfo1);
        SheetReadInfo sheetReadInfo2 = new SheetReadInfo();
        sheetReadInfo2.setRowStart(10);
        sheetReadInfo2.setColStart(3);
        sheetReadInfo2.setHeadNumber(sheetReadInfo2.getRowStart() + sheetReadInfo2.getHeadNumber() - 1);
        sheetReadInfoList.add(sheetReadInfo2);
        SheetReadInfo sheetReadInfo3 = new SheetReadInfo();
        sheetReadInfo3.setRowStart(10);
        sheetReadInfo3.setHeadNumber(0);
        sheetReadInfoList.add(sheetReadInfo3);
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
                LOGGER.error("fileName must be a doc/docx file");
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
            int index = sheetList.indexOf(readSheet);
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
