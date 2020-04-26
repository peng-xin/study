package pers.px;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Date;

public class DataTypeUtils {
    private static final Logger log = LoggerFactory.getLogger(DataTypeUtils.class);

    private static String parseInt(String num){
        String result=null;
        try {
            result=String.valueOf(TypeUtils.castToInt(num));
        } catch (JSONException e){
            log.error(MessageFormat.format("parseDateTime error.message is {0}", e.getMessage()));
        }
        return result;
    }

    private static String parseLong(String num){
        String result=null;
        try {
            result=String.valueOf(TypeUtils.castToInt(num));
        } catch (JSONException e){
            log.error(MessageFormat.format("parseDateTime error.message is {0}", e.getMessage()));
        }
        return result;
    }

    private static String parseFloat(String num){
        String result=null;
        try {
            result=String.valueOf(TypeUtils.castToInt(num));
        } catch (JSONException e){
            log.error(MessageFormat.format("parseDateTime error.message is {0}", e.getMessage()));
        }
        return result;
    }

    private static String parseDouble(String num){
        String result=null;
        try {
            result=String.valueOf(TypeUtils.castToInt(num));
        } catch (JSONException e){
            log.error(MessageFormat.format("parseDateTime error.message is {0}", e.getMessage()));
        }
        return result;
    }

    private static String parseDate(String date){
        String result=null;
        try {
            Date _date = TypeUtils.castToDate(date);
            result=String.format("%tF", _date);
        } catch (JSONException e){
            log.error(MessageFormat.format("parseDateTime error.message is {0}", e.getMessage()));
        }
        return result;
    }

    private static String parseDateTime(String datetime){
        String result=null;
        try {
            Date _datetime = TypeUtils.castToDate(datetime);
            result=String.format("%tF %tT", _datetime, _datetime);
        } catch (JSONException e){
            log.error(MessageFormat.format("parseDateTime error.message is {0}", e.getMessage()));
        }
        return result;
    }
}
