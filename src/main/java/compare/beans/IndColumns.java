package compare.beans;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author   yueshanfei
 * @date  2016年9月18日
 */
public class IndColumns {
    private String indexName;
    private String tableName;
    private List<String> columnName = Lists.newArrayList();
    private Map<Integer,String> columnNameMap = Maps.newHashMap();
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getIndexName() {
        return indexName;
    }
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
    public List<String> getColumnName() {
        return columnName;
    }
    public Map<Integer, String> getColumnNameMap() {
        return columnNameMap;
    }
}
