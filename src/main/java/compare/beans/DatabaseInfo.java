package compare.beans;

import java.util.LinkedHashMap;
import java.util.TreeMap;


public class DatabaseInfo {
    private  boolean single = false;
    private  LinkedHashMap<String, TreeMap<String ,Table>> tables=new  LinkedHashMap<String, TreeMap<String ,Table>>();
    private  LinkedHashMap<String, TreeMap<String ,Index>> indexs=new  LinkedHashMap<String, TreeMap<String ,Index>>();
    public TreeMap<String, Table> getTables(String owner) {
        if (null == owner) return null;
        return tables.get(owner.toUpperCase());
    }
    public LinkedHashMap<String, TreeMap<String, Table>> getTables() {
        return tables;
    }
    public LinkedHashMap<String, TreeMap<String, Index>> getIndexs() {
        return indexs;
    }
    public TreeMap<String, Index> getIndexs(String owner) {
        if (null == owner) return null;
        return indexs.get(owner.toUpperCase());
    }
    public boolean isSingle() {
        return single;
    }
    public void setSingle(boolean single) {
        this.single = single;
    }
    
}
