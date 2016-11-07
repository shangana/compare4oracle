package compare.beans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import com.google.common.collect.Maps;

public class PdmFileInfo {
    
    private List<String> users = new ArrayList<String>();
    private  LinkedHashMap<String, TreeMap<String ,Table>> tables=new  LinkedHashMap<String, TreeMap<String ,Table>>();
    private  LinkedHashMap<String, TreeMap<String ,Index>> indexs=new  LinkedHashMap<String, TreeMap<String ,Index>>();
    
    public void addTables(List<String> users, TreeMap<String, Table> t) {
        for (String string : users) {
            if (null == string || "".equals(string)) 
                continue;
            TreeMap<String, Table> map = tables.get(string.toUpperCase());
            if (null == map) {
                map = Maps.newTreeMap();
                tables.put(string.toUpperCase(), map);
            }
            map.putAll(t);
        }
    }
    
    public void addIndexs(List<String> users, TreeMap<String, Index> i) {
        for (String string : users) {
            if (null == string || "".equals(string)) 
                continue;
            TreeMap<String, Index> map = indexs.get(string.toUpperCase());
            if (null == map) {
                map = Maps.newTreeMap();
                indexs.put(string.toUpperCase(), map);
            }
            map.putAll(i);
        }
    }
    public TreeMap<String, Table> getTables(String owner) {
        if (null == owner) return null;
        return tables.get(owner.toUpperCase());
    }
    public LinkedHashMap<String, TreeMap<String, Table>> getTables() {
        return tables;
    }
    public TreeMap<String, Index> getIndexs(String owner) {
        if (null == owner) return null;
        return indexs.get(owner.toUpperCase());
    }
    public LinkedHashMap<String, TreeMap<String, Index>> getIndexs() {
        return indexs;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        for (String string : users) {
            if (null == string || "".equals(string))
                continue;
            if (!this.users.contains(string.toUpperCase().trim())) {
                this.users.add(string.toUpperCase().trim());
            }
        }
    }
    
}
