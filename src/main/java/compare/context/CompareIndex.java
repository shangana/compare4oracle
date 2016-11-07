package compare.context;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import com.google.common.collect.Lists;
import compare.beans.Index;

/**
 * @author   yueshanfei
 * @date  2016年9月22日
 */
public class CompareIndex extends SubmeterCompareThread {

    private CountDownLatch latch;
    private TreeMap<String, Index> compare;
    private TreeMap<String, Index> source;
    private String owner;
    private boolean submeter;
    private String pdmfile;
    private ThreadParam param;
    
    public CompareIndex(CountDownLatch latch) {
        this.latch=latch;
    }
    @Override
    public void run() {
        if (null == owner || (null == compare && null == source)) {
            latch.countDown();
            return;
        }
        List<DifferenceIndex> error = Lists.newArrayList();
        //比对出错误则添加到errors 对象列表中
        if (null == compare) {
            Iterator<Entry<String, Index>> iterator = source.entrySet().iterator();
            while(iterator.hasNext()) {
                Entry<String, Index> entry = iterator.next();
                String indexName = entry.getKey();
                DifferenceIndex i = new DifferenceIndex();
                error.add(i);
                Index index = entry.getValue();
                this.pdmfile= index.getPdmfile();
                i.setOwner(owner);
                i.setTypeId("12");
                i.setPdmfile(pdmfile);
                i.setSubmeter(submeter);
                i.setParentTable(getParentTable(index.getTableName()));
                i.setSourceTableName(index.getTableName());
                i.setSourceIndexName(indexName);
                i.setReferSQL("drop index "+ indexName);
            }
        }
        else if (null == source) {
            Iterator<Entry<String, Index>> iterator = compare.entrySet().iterator();
            while(iterator.hasNext()) {
                Entry<String, Index> entry = iterator.next();
                String indexName = entry.getKey();
                DifferenceIndex i = new DifferenceIndex();
                error.add(i);
                Index index = entry.getValue();
                this.pdmfile= index.getPdmfile();
                i.setOwner(owner);
                i.setTypeId("11");
                i.setPdmfile(pdmfile);
                i.setSubmeter(submeter);
                i.setParentTable(getParentTable(index.getTableName()));
                i.setCompareTableName(index.getTableName());
                i.setCompareIndexName(indexName);
                i.setReferSQL("create index "+ indexName);
            }
        } 
        else {
            //
            compare(source,compare,error,false);
            compare(compare,source,error,true);
        }
        param.getIndexerrors().addAll(error);
        latch.countDown();
    }
    
    
    private void compare(TreeMap<String, Index> map, TreeMap<String, Index> compareMap, List<DifferenceIndex> error,
            boolean reverse) {
        Iterator<Entry<String, Index>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Index> entry = iterator.next();
            Index index = entry.getValue();
            this.pdmfile= index.getPdmfile();
            String tableName = index.getTableName();
            String indexName = entry.getKey();
            if (submeter) {
                indexName = getParentTable(indexName);
            }
            Index compareIndex = compareMap.get(indexName);
            if (null == compareIndex) {
                DifferenceIndex i = new DifferenceIndex();
                error.add(i);
                i.setOwner(owner);
                i.setPdmfile(pdmfile);
                i.setSubmeter(submeter);
                i.setParentTable(getParentTable(index.getTableName()));
                if (reverse){ //如果反转 源表与目标表比对,则后面不比对
                    i.setTypeId("12");
                    i.setCompareTableName(index.getTableName());
                    i.setCompareIndexName(indexName);
                    i.setReferSQL("drop index "+ indexName);
                } else
                {
                    i.setTypeId("11");
                    i.setSourceTableName(index.getTableName());
                    i.setSourceIndexName(indexName);
                    i.setReferSQL("create index "+ indexName);
                }
            } else {
                if (reverse){ //如果反转 源表与目标表比对,则后面不比对
                    return;
                }
                //比对索引字段和顺序
                compareColumn(tableName,index,compareIndex, error);
                //比对唯一
                compareUnique(index, tableName, compareIndex, error);
                //比对主外键
                compareKey(index,tableName,compareIndex,error);
            }
        }
    }


    private void compareKey(Index index, String tableName, Index compareIndex, List<DifferenceIndex> error) {
        String key = index.getPrimaryKey();
        String compareKey = compareIndex.getPrimaryKey();
        if (null != key && !key.equals(compareKey)) {
            DifferenceIndex i = new DifferenceIndex();
            error.add(i);
            i.setPdmfile(pdmfile);
            i.setOwner(owner);
            i.setTypeId("15");
            i.setSubmeter(submeter);
            i.setParentTable(getParentTable(tableName));
            i.setSourceTableName(tableName);
            i.setSourceIndexName(index.getIndexName());
            i.setSourceField(getColumnNames(index.getColumnName()));
            i.setSourceUnique(key);
            i.setCompareTableName(tableName);
            i.setCompareIndexName(compareIndex.getIndexName());
            i.setCompareField(getColumnNames(compareIndex.getColumnName()));
            i.setCompareUnique(compareKey);
            i.setReferSQL("暂无参考");
        }
    }


    private void compareUnique(Index index, String tableName, Index compareIndex, List<DifferenceIndex> error) {
        String uniqueness = index.getUniqueness();
        String compareUnique = compareIndex.getUniqueness();
        if (null != uniqueness && !uniqueness.equals(compareUnique)) {
            DifferenceIndex i = new DifferenceIndex();
            error.add(i);
            i.setPdmfile(pdmfile);
            i.setOwner(owner);
            i.setTypeId("14");
            i.setSourceTableName(tableName);
            i.setSubmeter(submeter);
            i.setParentTable(getParentTable(tableName));
            i.setSourceIndexName(index.getIndexName());
            i.setSourceField(getColumnNames(index.getColumnName()));
            i.setSourceUnique(index.getUniqueness());
            i.setCompareTableName(tableName);
            i.setCompareIndexName(compareIndex.getIndexName());
            i.setCompareField(getColumnNames(compareIndex.getColumnName()));
            i.setCompareUnique(compareIndex.getUniqueness());
            i.setReferSQL("暂无参考");
        }
    }


    private void compareColumn(String tableName, Index index, Index compareIndex,
             List<DifferenceIndex> error) {
        List<String> columnNames = index.getColumnName();
        List<String> compareNames = compareIndex.getColumnName();
        for (int idx = 0; idx < columnNames.size(); idx++) {
            String columnName = columnNames.get(idx);
             
            if (null == columnName || columnNames.size() != compareNames.size()
                    || !columnName.equalsIgnoreCase(compareNames.get(idx))) {
                DifferenceIndex i = new DifferenceIndex();
                error.add(i);
                i.setPdmfile(pdmfile);
                i.setOwner(owner);
                i.setTypeId("13");
                i.setSourceTableName(tableName);
                i.setSubmeter(submeter);
                i.setParentTable(getParentTable(tableName));
                i.setSourceIndexName(index.getIndexName());
                i.setSourceField(getColumnNames(columnNames));
                i.setCompareTableName(tableName);
                i.setCompareIndexName(compareIndex.getIndexName());
                i.setCompareField(getColumnNames(compareNames));
                i.setReferSQL("暂无参考");
                return;
            }
        }
    }


    private String getColumnNames(List<String> columnNames) {
        StringBuffer sb = new StringBuffer();
        for (String string : columnNames) {
            sb.append(string).append(",");
        }
        return sb.substring(0, sb.length()-1);
    }

    
    public void setCompare(TreeMap<String, Index> compare) {
        this.compare=compare;
    }
    public void setSource(TreeMap<String, Index> source) {
        this.source=source;
    }
    public void setOwner(String owner) {
        this.owner=owner;
    }
    public void isSubmeter(boolean submeter) {
        this.submeter=submeter;
    }
    public void setParam(ThreadParam param) {
        this.param = param;
    }
}
