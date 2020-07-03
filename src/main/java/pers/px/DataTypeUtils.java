package pers.px;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;

public class DataTypeUtils {
    private static final Logger log = LoggerFactory.getLogger(DataTypeUtils.class);

    public static Integer parseInt(String num) {
        Integer result = null;
        try {
            result = TypeUtils.castToInt(num);
        } catch (JSONException | NumberFormatException e) {
            log.error(MessageFormat.format("Source param is {0}.parseInt error.message is {1}", num, e.getMessage()));
        }
        return result;
    }

    public static Long parseLong(String num) {
        Long result = null;
        try {
            result = TypeUtils.castToLong(num);
        } catch (JSONException | NumberFormatException e) {
            log.error(MessageFormat.format("Source param is {0}.parseLong error.message is {1}", num, e.getMessage()));
        }
        return result;
    }

    public static Float parseFloat(String num) {
        Float result = null;
        try {
            result = TypeUtils.castToFloat(num);
        } catch (JSONException | NumberFormatException e) {
            log.error(MessageFormat.format("Source param is {0}.parseFloat error.message is {1}", num, e.getMessage()));
        }
        return result;
    }

    public static Double parseDouble(String num) {
        Double result = null;
        try {
            result = TypeUtils.castToDouble(num);
        } catch (JSONException | NumberFormatException e) {
            log.error(MessageFormat.format("Source param is {0}.parseDouble error.message is {1}", num, e.getMessage()));
        }
        return result;
    }

    public static BigDecimal parseBigDecimal(String num) {
        BigDecimal result = null;
        try {
            result = TypeUtils.castToBigDecimal(num);
        } catch (JSONException | NumberFormatException e) {
            log.error(MessageFormat.format("Source param is {0}.parseBigDecimal error.message is {1}", num, e.getMessage()));
        }
        return result;
    }

    public static String parseDate(String date) {
        String result = null;
        try {
            Date _date = TypeUtils.castToDate(date);
            result = (null == _date ? null : String.format("%tF", _date));
        } catch (JSONException | NumberFormatException e) {
            log.error(MessageFormat.format("Source param is {0}.parseDate error.message is {1}", date, e.getMessage()));
        }
        return result;
    }

    public static String parseDateTime(String datetime) {
        String result = null;
        try {
            Date _datetime = TypeUtils.castToDate(datetime);
            result = (null == _datetime ? null : String.format("%tF %tT", _datetime, _datetime));
        } catch (JSONException | NumberFormatException e) {
            log.error(MessageFormat.format("Source param is {0}.parseDateTime error.message is {1}", datetime, e.getMessage()));
        }
        return result;
    }
}
