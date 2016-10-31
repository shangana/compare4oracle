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
            TreeMap<String, Table> map = tables.get(string);
            if (null == map) {
                map = Maps.newTreeMap();
                tables.put(string, map);
            }
            map.putAll(t);
        }
    }
    
    public void addIndexs(List<String> users, TreeMap<String, Index> i) {
        for (String string : users) {
            TreeMap<String, Index> map = indexs.get(string);
            if (null == map) {
                map = Maps.newTreeMap();
                indexs.put(string, map);
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
            if (!this.users.contains(string)) {
                this.users.add(string);
            }
        }
        this.users = users;
    }
    
}
