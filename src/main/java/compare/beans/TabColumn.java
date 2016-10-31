package compare.beans;

/**
 * @author   yueshanfei
 * @date  2016年9月18日
 */
public class TabColumn {
    private String tableName;
    private String columnId;
    private String columnName;
    private String nullable;
    private String columnType;
    private String dataDefault;
    private String characterSetName;
//    private String dataType;
//    private Integer dataLength;
//    private Integer dataScale;
//    private Integer dataPrecision;
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getColumnId() {
        return columnId;
    }
    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }
    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public String getColumnType() {
        return columnType;
    }
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
    public String getDataDefault() {
        return dataDefault;
    }
    public void setDataDefault(String dataDefault) {
        if (null == dataDefault || "".equals(dataDefault) || dataDefault.equalsIgnoreCase("null")){
            this.dataDefault="";
            return;
        }
        String s[];
        if (dataDefault.contains("\"")) {
            s = dataDefault.split("\"");
            if (s.length < 1) {
                this.dataDefault= "";
                return;
            } else {
                dataDefault = s[1];
            }
        }
        if (dataDefault.contains("\'")) {
            s = dataDefault.split("\'");
            if (s.length < 1) {
                this.dataDefault= "";
                return;
            } else {
                dataDefault = s[1];
            }
        }
        this.dataDefault = dataDefault;
    }
    public String getCharacterSetName() {
        return characterSetName;
    }
    public void setCharacterSetName(String characterSetName) {
        this.characterSetName = characterSetName;
    }
    public String getNullable() {
        return nullable;
    }
    public void setNullable(String nullable) {
        this.nullable = nullable;
    }
    
}
