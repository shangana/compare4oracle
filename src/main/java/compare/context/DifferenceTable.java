package compare.context;

/**
 * @author   yueshanfei
 * @date  2016年9月22日
 */
public class DifferenceTable {
    private String pdmfile;
    private String typeId;
    private String owner;
    private boolean submeter;
    private String parentTable;
    private String sourseTable;
    private String sourseField;
    private String sourseType;
    private String sourseDefault;
    private String sourceNullabel;
    private String compareTable;
    private String compareField;
    private String compareType;
    private String compareDefault;
    private String compareNullable;
    private String referSQL;
    public String getTypeId() {
        return typeId;
    }
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
    public String getSourseTable() {
        return null == sourseTable? "":sourseTable;
    }
    public void setSourseTable(String sourseTable) {
        this.sourseTable = sourseTable;
    }
    public String getSourseField() {
        return null == sourseField?"":sourseField;
    }
    public void setSourseField(String sourseField) {
        this.sourseField = sourseField;
    }
    public String getSourseType() {
        return null==sourseType?"":sourseType;
    }
    public void setSourseType(String sourseType) {
        this.sourseType = sourseType;
    }
    public String getSourseDefault() {
        return null==sourseDefault?"":sourseDefault;
    }
    public void setSourseDefault(String sourseDefault) {
        this.sourseDefault = sourseDefault;
    }
    public String getCompareTable() {
        return null==compareTable?"":compareTable;
    }
    public void setCompareTable(String compareTable) {
        this.compareTable = compareTable;
    }
    public String getCompareField() {
        return null==compareField?"":compareField;
    }
    public void setCompareField(String compareField) {
        this.compareField = compareField;
    }
    public String getCompareType() {
        return null==compareType?"":compareType;
    }
    public void setCompareType(String compareType) {
        this.compareType = compareType;
    }
    public String getCompareDefault() {
        return null==compareDefault?"":compareDefault;
    }
    public void setCompareDefault(String compareDefault) {
        this.compareDefault = compareDefault;
    }
    public String getReferSQL() {
        return null==referSQL?"":referSQL;
    }
    public void setReferSQL(String referSQL) {
        this.referSQL = referSQL;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getPdmfile() {
        return null ==pdmfile?"":pdmfile;
    }
    public void setPdmfile(String pdmfile) {
        this.pdmfile = pdmfile;
    }
    public boolean isSubmeter() {
        return submeter;
    }
    public void setSubmeter(boolean submeter) {
        this.submeter = submeter;
    }
    public String getParentTable() {
        return null==parentTable?"":parentTable;
    }
    public void setParentTable(String parentTable) {
        this.parentTable = parentTable;
    }
    public String getSourceNullabel() {
        return null==sourceNullabel?"":sourceNullabel;
    }
    public void setSourceNullabel(String sourceNullabel) {
        this.sourceNullabel = sourceNullabel;
    }
    public String getCompareNullable() {
        return null == compareNullable?"":compareNullable;
    }
    public void setCompareNullable(String compareNullable) {
        this.compareNullable = compareNullable;
    }
    
    
    
}
