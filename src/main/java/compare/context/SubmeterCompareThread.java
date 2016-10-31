package compare.context;

import java.util.regex.Pattern;

/**
 * @author   yueshanfei
 * @date  2016年10月27日
 */
public class SubmeterCompareThread extends Thread {

    protected String getParentTable(String tableName) {
        String[] split = tableName.split("_");
        if (split.length == 1) {
            return tableName;
        }
        String regex = "[0-9]*";
        Pattern pat = Pattern.compile(regex);
        String[] str = pat.split(split[split.length - 1]);
        int lastIndexOf = tableName.lastIndexOf("_");
        if (str.length == 0) {
            return getParentTable(tableName.substring(0, lastIndexOf));
        }
        else {
            return tableName;
        }
    }
}
