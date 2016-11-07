package compare.beans.definition;

import java.util.List;
import java.util.Map;

/**
 * 比对双方用户对等关系
 * @author   yueshanfei
 * @date  2016年10月26日
 */
public class Equality {
    private List<String> usernames;
    private Map<String,Database> dbmap;
    private Map<String,Database> sourcemap;
    public List<String> getUsernames() {
        return usernames;
    }
    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }
    public Map<String, Database> getDbmap() {
        return dbmap;
    }

    public void setDbmap(Map<String, Database> dbmap) {
        this.dbmap = dbmap;
    }
    public Map<String, Database> getSourcemap() {
        return sourcemap;
    }
    public void setSourcemap(Map<String, Database> sourcemap) {
        this.sourcemap = sourcemap;
    }

}
