package compare.context;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import com.google.common.collect.Lists;
import compare.beans.TabColumn;
import compare.beans.Table;

/**
 * @author   yueshanfei
 * @date  2016年9月22日
 */
public class CompareTable extends SubmeterCompareThread{
    private CountDownLatch latch;
    private TreeMap<String, Table> source;
    private TreeMap<String, Table> compare;
    private List<DifferenceTable> errors;
    private boolean submeter = false;
    private String owner;
    private String pdmfile;
    
    public CompareTable(CountDownLatch latch) {
        this.latch = latch;
    }
    @Override
    public void run() {
        if (null == owner) {
            latch.countDown();
            return;
        }
        List<DifferenceTable> error = Lists.newArrayList();
        //比对出错误则添加到errors 对象列表中
        if (null == compare) {
            Iterator<Entry<String, Table>> iterator = source.entrySet().iterator();
            while (iterator.hasNext()) {
                DifferenceTable t = new DifferenceTable();
                error.add(t);
                Entry<String, Table> next = iterator.next();
                Table table = next.getValue();
                this.pdmfile = table.getPdmfilepath();
                t.setOwner(owner);
                t.setSubmeter(submeter);
                t.setParentTable(getParentTable(table.getTableName()));
                t.setPdmfile(pdmfile);
                t.setTypeId("1"); //pdm 存在 compare 不存在
                t.setSourseTable(table.getTableName());
                t.setReferSQL("create table "+ table.getTableName());
            }
        }
        else if (null == source) {
            Iterator<Entry<String, Table>> iterator = compare.entrySet().iterator();
            while (iterator.hasNext()) {
                DifferenceTable t = new DifferenceTable();
                error.add(t);
                Entry<String, Table> next = iterator.next();
                Table table = next.getValue();
                this.pdmfile = table.getPdmfilepath();
                t.setOwner(owner);
                t.setSubmeter(submeter);
                t.setParentTable(getParentTable(table.getTableName()));
                t.setPdmfile(pdmfile);
                t.setTypeId("2"); //pdm 不存在 compare 存在
                t.setCompareTable(table.getTableName());
                t.setReferSQL("drop table "+ table.getTableName());
            }
        }
        else {
            //两个对象比对
            compare(source, compare, error, false);
            compare(compare, source, error, true);
        }
        
        errors.addAll(error);
        //
        latch.countDown();
    }

    private void compare(TreeMap<String, Table> map, TreeMap<String, Table> compareMap, List<DifferenceTable> error, boolean reverse) {
        Iterator<Entry<String, Table>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Table> entry2 = iterator.next();
            String tableName = entry2.getKey();
            Table table = entry2.getValue();
            if (submeter) {
                tableName = getParentTable(tableName);
            }
            Table compareTable = compareMap.get(tableName);
            this.pdmfile = table.getPdmfilepath();
            if (null == this.pdmfile) {
                this.pdmfile = (null == compareTable) ? "" : compareTable.getPdmfilepath();
            }
            if (null == compareTable) {
                DifferenceTable t = new DifferenceTable();
                error.add(t);
                t.setSubmeter(submeter);
                t.setPdmfile(pdmfile);
                t.setOwner(owner);
                if (reverse) {
                    t.setParentTable("");
                    t.setTypeId("2");
                    t.setSourseTable("");
                    t.setCompareTable(tableName);
                    t.setReferSQL("drop table " + tableName);
                }
                else {
                    t.setParentTable(tableName.equals(table.getTableName()) ? "" : tableName);
                    t.setTypeId("1");
                    t.setSourseTable(table.getTableName());
                    t.setReferSQL("create table " + tableName);
                }
                
            } else {
                compareField(tableName,table, compareTable, error, reverse);
            }
        }
    }
    
    private void compareField(String parentTableName, Table table, Table compareTable, List<DifferenceTable> error, boolean reverse) {
        String tableName = table.getTableName();
        String compareTableName = compareTable.getTableName();
        LinkedHashMap<String, TabColumn> columnMap = table.getColumns();
        LinkedHashMap<String, TabColumn> compareColumnMap = compareTable.getColumns();
        Iterator<Entry<String, TabColumn>> iterator = columnMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, TabColumn> next = iterator.next();
            String columnName = next.getKey();
            TabColumn column = next.getValue();
            if (!reverse) {
                columnName = column.getColumnName();
            }
            TabColumn compareTabColumn = compareColumnMap.get(columnName);
            if (null == compareTabColumn) {
                DifferenceTable t = new DifferenceTable();
                error.add(t);
                t.setOwner(owner);
                t.setSubmeter(submeter);
                if (reverse) {
                    t.setParentTable(parentTableName.equals(compareTableName) ? "" : parentTableName);
                    t.setPdmfile(pdmfile);
                    t.setTypeId("4");
                    t.setSourseTable(compareTableName);
                    t.setCompareTable(tableName);
                    t.setCompareField(columnName);
                    t.setReferSQL("alter table " + tableName + " drop column " + columnName);
                }
                else {
                    t.setParentTable(parentTableName.equals(tableName) ? "" : parentTableName);
                    t.setPdmfile(pdmfile);
                    t.setTypeId("3");
                    t.setSourseTable(tableName);
                    t.setSourseField(columnName);
                    t.setCompareTable(compareTableName);
                    t.setReferSQL("alter table " + tableName + " add " + columnName);
                }
                
            } else {
                //两个表名字段一致后,不需要颠倒执行
                if (reverse) {
                    continue;
                }
                //比对类型 5
                compareType(parentTableName,tableName, compareTabColumn.getTableName(),column,compareTabColumn, error);
                //比对是否为空 6 
                compareNull(parentTableName,tableName, compareTableName, column, compareTabColumn, error);
                //比对默认值 7 
                compareDefault(parentTableName,tableName, compareTableName, column, compareTabColumn, error);
                //比对字段顺序 9
                compareOrder(parentTableName,tableName, compareTableName,column,compareTabColumn, error);
            }
        }
    }
    private void compareType(String parentTableName, String tableName,String compareTableName, TabColumn column, TabColumn compareTabColumn,
            List<DifferenceTable> error) {
        String columnName = column.getColumnName();
        String columnType = column.getColumnType();
        String comapreColumnType = compareTabColumn.getColumnType();
        //pdm中char类型到数据库中成为char(1)
        if ("CHAR".equalsIgnoreCase(columnType) && "CHAR(1)".equalsIgnoreCase(comapreColumnType)) {
            return;
        }
        if (!columnType.equalsIgnoreCase(comapreColumnType)) {
            DifferenceTable t = new DifferenceTable();
            error.add(t);
            t.setOwner(owner);
            t.setSubmeter(submeter);
            t.setPdmfile(pdmfile);
            t.setReferSQL("暂无参考");
            t.setParentTable(parentTableName.equals(tableName)?"":parentTableName);
            t.setTypeId("5");
            t.setSourseTable(tableName);
            t.setSourseField(columnName);
            t.setSourseType(columnType);
            t.setCompareTable(compareTableName);
            t.setCompareField(compareTabColumn.getColumnName());
            t.setCompareType(compareTabColumn.getColumnType());
        } 
        
    }
    private void compareNull(String parentTableName, String tableName, String compareTableName, TabColumn column,
            TabColumn compareTabColumn, List<DifferenceTable> error) {
        String nullable = column.getNullable();
        String comapreNullable = compareTabColumn.getNullable();
        if (!nullable.equalsIgnoreCase(comapreNullable)) {
            DifferenceTable t = new DifferenceTable();
            error.add(t);
            t.setSubmeter(submeter);
            t.setOwner(owner);
            t.setPdmfile(pdmfile);
            t.setReferSQL("暂无参考");
            t.setParentTable(parentTableName.equals(tableName)?"":parentTableName);
            t.setTypeId("6");
            t.setSourseTable(tableName);
            t.setSourseField(column.getColumnName());
            t.setSourseType(column.getColumnType());
            t.setSourseDefault(column.getDataDefault());
            t.setSourceNullabel(nullable);
            t.setCompareTable(compareTableName);
            t.setCompareField(compareTabColumn.getColumnName());
            t.setCompareType(compareTabColumn.getColumnType());
            t.setCompareDefault(compareTabColumn.getDataDefault());
            t.setCompareNullable(comapreNullable);
        }
    }
    private void compareDefault(String parentTableName, String tableName, String compareTableName, TabColumn column,
            TabColumn compareTabColumn, List<DifferenceTable> error) {
        //
        String dataDefault = column.getDataDefault();
        String compareDataDefault = compareTabColumn.getDataDefault();
        if (!dataDefault.equalsIgnoreCase(compareDataDefault)) {
            DifferenceTable t = new DifferenceTable();
            error.add(t);
            t.setSubmeter(submeter);
            t.setOwner(owner);
            t.setPdmfile(pdmfile);
            t.setReferSQL("暂无参考");
            t.setParentTable(parentTableName.equals(tableName)?"":parentTableName);
            t.setTypeId("7");
            t.setSourseTable(tableName);
            t.setSourseField(column.getColumnName());
            t.setSourseType(column.getColumnType());
            t.setSourseDefault(dataDefault);
            t.setCompareTable(compareTableName);
            t.setCompareField(compareTabColumn.getColumnName());
            t.setCompareType(compareTabColumn.getColumnType());
            t.setCompareDefault(compareTabColumn.getDataDefault());
        }
    }
    private void compareOrder(String parentTableName,String tableName, String compareTableName, TabColumn column, TabColumn compareTabColumn,
            List<DifferenceTable> error) {
        String columnId = column.getColumnId();
        String compareColumnId = compareTabColumn.getColumnId();
        if (!columnId.equalsIgnoreCase(compareColumnId)) {
            DifferenceTable t = new DifferenceTable();
            error.add(t);
            t.setTypeId("9");
            t.setSubmeter(submeter);
            t.setParentTable(parentTableName.equals(tableName)?"":parentTableName);
            t.setPdmfile(pdmfile);
            t.setOwner(owner);
            t.setSourseTable(tableName);
            t.setSourseField(column.getColumnName() +"["+columnId+"]");
            t.setSourseType(column.getColumnType());
            t.setSourseDefault(column.getDataDefault());
            t.setCompareTable(compareTableName);
            t.setCompareField(compareTabColumn.getColumnName()+"["+compareColumnId+"]");
            t.setCompareType(compareTabColumn.getColumnType());
            t.setCompareDefault(compareTabColumn.getDataDefault());
            t.setReferSQL("暂无参考");
        }
    }
    
    
    public void setCompare(TreeMap<String, Table> compare) {
        this.compare = compare;
    }
    public void setErrors(List<DifferenceTable> errors) {
        this.errors = errors;
    }
    public void isSubmeter(boolean submeter) {
        this.submeter = submeter;
    }
    public void setSource(TreeMap<String, Table> source) {
        this.source=source;
    }
    public void setOwner(String owner) {
        this.owner=owner;
    }
}
