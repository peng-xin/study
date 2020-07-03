package pers.px;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

import static com.alibaba.excel.enums.CellDataTypeEnum.NUMBER;

public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoModelDataListener.class);
    private static final Pattern NUMBER_PATTERN=Pattern.compile("^([+-]?)([1-9]\\d*)(\\.\\d)?[eE]([+-]?)(\\d+)|[+-]?\\d+(\\.\\d*)?|[+-]?\\.\\d+$");

    private List<String[]> dataList = new LinkedList<>();
    private List<String[]> headList = new LinkedList<>();

    private SheetReadInfo sheetReadInfo;

    private Integer rowStart = 1;
    private Integer rowEnd = 1;
    private Integer colStart = 1;
    private Integer colEnd = 1;
    private Integer headNumber = 1;

    private Map<Integer, CellDataTypeEnum> schema = new HashMap<>();

    public NoModelDataListener() {

    }

    public NoModelDataListener(SheetReadInfo sheetReadInfo) {
        this.sheetReadInfo = sheetReadInfo;
        this.rowStart = sheetReadInfo.getRowStart() > 0 ? sheetReadInfo.getRowStart() - 1 : 0;
        this.colStart = sheetReadInfo.getColStart() > 0 ? sheetReadInfo.getColStart() - 1 : 0;
        this.headNumber = sheetReadInfo.getHeadNumber();
    }

    public List<String[]> getDataList() {
        return dataList;
    }

    public void setDataList(List<String[]> dataList) {
        this.dataList = dataList;
    }

    public List<String[]> getHeadList() {
        return headList;
    }

    public void setHeadList(List<String[]> headList) {
        this.headList = headList;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        if (context.readRowHolder().getRowIndex() <= rowStart) {
            return;
        }
        dataList.add(readData(data, context));
//        LOGGER.info("row {}'s data is {}", context.readRowHolder().getRowIndex(), data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        sheetReadInfo.setName(context.readSheetHolder().getSheetName());
//        sheetReadInfo.setSchema(headList);
//        sheetReadInfo.setData(dataList);
        LOGGER.info("sheet No.{} read finish", context.readSheetHolder().getSheetNo());
        LOGGER.info("sheet name is {}", context.readSheetHolder().getSheetName());
//        LOGGER.info("head is {}", Arrays.toString(headList.stream().map(head -> String.join(",", head)).toArray()));
//        LOGGER.info("data is {}", Arrays.toString(dataList.stream().map(data -> String.join(",", data)).toArray()));
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        super.invokeHeadMap(headMap, context);
        headList.add(getData(headMap));
        LOGGER.info("invokeHeadMap is {}", headMap);
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        Map<Integer, Cell> cellMap = context.readRowHolder().getCellMap();
        return !CollectionUtils.isEmpty(cellMap);
    }

    private String[] readData(Map<Integer, String> data, AnalysisContext context) {

        ReadRowHolder readRowHolder = context.readRowHolder();
        Integer rowIndex = readRowHolder.getRowIndex();
        Map<Integer, Cell> cellMap = readRowHolder.getCellMap();

        String[] arr = new String[cellMap.keySet().stream().max(Integer::compareTo).get() + 1];

        for (Map.Entry<Integer, Cell> entry : cellMap.entrySet()) {
            Integer cellIndex = entry.getKey();
            CellData cell = (CellData) entry.getValue();
            arr[cellIndex] = readData(cellIndex,cell);
        }
        System.out.println();
        System.out.println();
        System.out.println();
        return arr;
    }

    private String[] getData(Map<Integer, String> headMap) {
        String[] arr = new String[headMap.size() - colStart];
        for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
            if (entry.getKey() >= colStart) {
                arr[entry.getKey() - colStart] = entry.getValue();
            }
        }
        return arr;
    }

    private Map<String, String> buildHead() {
        Map<String, String> result = new LinkedHashMap<>();
        for (String[] head : headList) {
            LOGGER.info("head {}", Arrays.toString(head));
        }
        return result;
    }

    private String readData(Integer cellIndex,CellData cellData) {
        String data = null;
        switch (cellData.getType()) {
            case NUMBER:
                data = cellData.getNumberValue().toString();
                schema.put(cellData.getColumnIndex(),cellData.getType());
                break;
            case BOOLEAN:
                data = cellData.getBooleanValue().toString();
                break;
            case DIRECT_STRING:
            case STRING:
            case ERROR:
                data = cellData.getStringValue();
                if(NUMBER_PATTERN.matcher(data).matches()){
                    schema.put(cellIndex,NUMBER);
                }
                String tmp=DataTypeUtils.parseDateTime(data);
                if (tmp != null) {
                    schema.put(cellIndex,NUMBER);
                }
                break;
            case IMAGE:
            default:
                data = StringUtils.EMPTY;
                break;
        }
        LOGGER.info(cellData.getColumnIndex() + "=>" + cellData.getType() + "=>" + data);
        return data;
    }
}
