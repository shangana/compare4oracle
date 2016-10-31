package compare.beans;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author   yueshanfei
 * @date  2016年9月20日
 */
public class XMLTable {
    private String pdmfile; //归属的pdm文件路径
    private String code;
    private TreeMap<String, TabColumn> columns;
    private Map<String,String> refIdMap = Maps.newHashMap();
    private TreeMap<String, XMLKey> keys;
    private String primaryKey;
    private List<XMLIndex> indexs;
    private List<String> users;
    
    public TreeMap<String,Table> toTable() {
        TreeMap<String ,Table> tableMap = Maps.newTreeMap();
        Table table = new Table();
        tableMap.put(code, table);
        table.setTableName(code);
//        table.setColumns(Maps.newLinkedHashMap(columns));
        LinkedHashMap<String, TabColumn> columnMap = Maps.newLinkedHashMap();
        table.setColumns(columnMap);
        Iterator<Entry<String, TabColumn>> iterator = columns.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, TabColumn> next = iterator.next();
            TabColumn column = next.getValue();
            columnMap.put(column.getColumnName(), column);
        }
        table.setPdmfilepath(pdmfile);
        return tableMap;
    }
    
    public TreeMap<String, Index> toIndex() {
        TreeMap<String, Index> indexMap = Maps.newTreeMap();
        if (null != indexs) {
            for (XMLIndex xmlindex : indexs) {
                String indexName = xmlindex.getCode();
                Index index = new Index();
                indexMap.put(indexName, index);
                index.setPdmfile(pdmfile);
                index.setTableName(code);
                index.setIndexName(indexName);
                index.setUniqueness(xmlindex.getUnique());
                List<String> columnRefIds = xmlindex.getIndexColumnRefIds();
                List<String> columnName = Lists.newArrayList();
                for (String refId : columnRefIds) {
                    columnName.add(refIdMap.get(refId));
                }
                index.setColumnName(columnName);
            }
        }
        if (null != keys) {
            for (XMLKey xmlKey : keys.values()) {
                String indexName = xmlKey.getConstraintName();
                Index index = new Index();
                indexMap.put(indexName, index);
                index.setIndexName(indexName);
                index.setPdmfile(pdmfile);
                index.setTableName(code);
                if (null != primaryKey && null != keys.get(primaryKey)) {
                    index.setPrimaryKey("PRIMARY KEY");
                } else {
                    index.setUniqueness("UNIQUE");
                }
                List<String> columnRefIds = xmlKey.getColumnRefIds();
                List<String> columnName = Lists.newArrayList();
                for (String refId : columnRefIds) {
                    columnName.add(refIdMap.get(refId));
                }
                index.setColumnName(columnName);
            }
        }
        return indexMap;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getPrimaryKey() {
        return primaryKey;
    }
    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
    public List<XMLIndex> getIndexs() {
        return indexs;
    }
    public void setIndexs(List<XMLIndex> indexs) {
        this.indexs = indexs;
    }
    public TreeMap<String, TabColumn> getColumns() {
        return columns;
    }
    public void setColumns(TreeMap<String, TabColumn> columns) {
        this.columns = columns;
    }
    public void setKeys(TreeMap<String, XMLKey> keys) {
        this.keys = keys;
    }
    public List<String> getUsers() {
        return users;
    }
    public void setUsers(List<String> users) {
        this.users = users;
    }


    public void setPdmfile(String filepath) {
        this.pdmfile = filepath;
    }

    public void setRefIdMap(Map<String, String> refIdMap) {
        this.refIdMap = refIdMap;
    }
    
}
