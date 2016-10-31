package compare.beans;

import java.util.List;

/**
 * @author   yueshanfei
 * @date  2016年9月19日
 */
public class XMLKey {
    private String id;
    private String constraintName;
    private List<String> columnRefIds;
    public String getConstraintName() {
        return constraintName;
    }
    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }
    public List<String> getColumnRefIds() {
        return columnRefIds;
    }
    public void setColumnRefIds(List<String> columnRefIds) {
        this.columnRefIds = columnRefIds;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
