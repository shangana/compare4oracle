package compare.beans;

import java.util.List;

/**
 * @author   yueshanfei
 * @date  2016年9月18日
 */
public class Index {
    private String indexName;
    private String tableName;
    private String uniqueness;
    private List<String> columnName;
    private String primaryKey;
    private String pdmfile;
    
    public String getIndexName() {
        return indexName;
    }
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getUniqueness() {
        return uniqueness;
    }
    public void setUniqueness(String uniqueness) {
        this.uniqueness = uniqueness;
    }
    public List<String> getColumnName() {
        return columnName;
    }
    public void setColumnName(List<String> columnName) {
        this.columnName = columnName;
    }
    public String getPrimaryKey() {
        return primaryKey;
    }
    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
    public String getPdmfile() {
        return pdmfile;
    }
    public void setPdmfile(String pdmfile) {
        this.pdmfile = pdmfile;
    }
    
}
