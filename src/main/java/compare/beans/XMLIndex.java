package compare.beans;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author   yueshanfei
 * @date  2016年9月20日
 */
public class XMLIndex {
    private String tableName;
    private String code;
    private List<String> indexColumnRefIds = Lists.newArrayList();
    private String unique; //存在,则是主键
    private String linkedObjectReferenceRefId;
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public List<String> getIndexColumnRefIds() {
        return indexColumnRefIds;
    }
    public void setIndexColumnRefIds(List<String> indexColumnRefIds) {
        this.indexColumnRefIds = indexColumnRefIds;
    }
    public String getUnique() {
        return unique;
    }
    public void setUnique(String unique) {
        this.unique = unique;
    }
    public String getLinkedObjectReferenceRefId() {
        return linkedObjectReferenceRefId;
    }
    public void setLinkedObjectReferenceRefId(String linkedObjectReferenceRefId) {
        this.linkedObjectReferenceRefId = linkedObjectReferenceRefId;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
