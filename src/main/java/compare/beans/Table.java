package compare.beans;

import java.util.LinkedHashMap;

/**
 * @author   yueshanfei
 * @date  2016年9月18日
 */
public class Table {
    private String pdmfilepath;
    private String owner;
    private String tableName;
    private LinkedHashMap<String, TabColumn> columns;
    
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public LinkedHashMap<String, TabColumn> getColumns() {
        return columns;
    }
    public void setColumns(LinkedHashMap<String, TabColumn> columns) {
        this.columns = columns;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getPdmfilepath() {
        return pdmfilepath;
    }
    public void setPdmfilepath(String pdmfilepath) {
        this.pdmfilepath = pdmfilepath;
    }
    
}
