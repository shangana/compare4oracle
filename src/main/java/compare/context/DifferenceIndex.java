package compare.context;

/**
 * @author   yueshanfei
 * @date  2016年9月23日
 */
public class DifferenceIndex {
    private String pdmfile;
    private String owner;
    private String typeId;
    private boolean submeter;
    private String parentTable;
    private String sourceTableName;
    private String sourceIndexName;
    private String sourceField;
    private String sourceUnique;
    private String compareTableName;
    private String compareIndexName;
    private String compareField;
    private String compareUnique;
    private String referSQL;
    public String getOwner() {
        return null==owner?"":owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getTypeId() {
        return null==typeId?"":typeId;
    }
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
    public String getSourceTableName() {
        return null==sourceTableName?"":sourceTableName;
    }
    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }
    public String getSourceIndexName() {
        return null==sourceIndexName?"":sourceIndexName;
    }
    public void setSourceIndexName(String sourceIndexName) {
        this.sourceIndexName = sourceIndexName;
    }
    public String getSourceField() {
        return null==sourceField?"":sourceField;
    }
    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }
    public String getSourceUnique() {
        return null==sourceUnique?"":sourceUnique;
    }
    public void setSourceUnique(String sourceUnique) {
        this.sourceUnique = sourceUnique;
    }
    public String getCompareTableName() {
        return null==compareTableName?"":compareTableName;
    }
    public void setCompareTableName(String compareTableName) {
        this.compareTableName = compareTableName;
    }
    public String getCompareIndexName() {
        return null==compareIndexName?"":compareIndexName;
    }
    public void setCompareIndexName(String compareIndexName) {
        this.compareIndexName = compareIndexName;
    }
    public String getCompareField() {
        return null==compareField?"":compareField;
    }
    public void setCompareField(String compareField) {
        this.compareField = compareField;
    }
    public String getCompareUnique() {
        return null==compareUnique?"":compareUnique;
    }
    public void setCompareUnique(String compareUnique) {
        this.compareUnique = compareUnique;
    }
    public String getReferSQL() {
        return null==referSQL?"":referSQL;
    }
    public void setReferSQL(String referSQL) {
        this.referSQL = referSQL;
    }
    public void setPdmfile(String pdmfile) {
        this.pdmfile = pdmfile;
    }
    public String getPdmfile() {
        return null==pdmfile?"":pdmfile;
    }
    public boolean isSubmeter() {
        return submeter;
    }
    public void setSubmeter(boolean submeter) {
        this.submeter = submeter;
    }
    public String getParentTable() {
        return parentTable;
    }
    public void setParentTable(String parentTable) {
        this.parentTable = parentTable;
    }
    
}
