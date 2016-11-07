package compare.context;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author   yueshanfei
 * @date  2016年11月1日
 */
public class ThreadParam {

    private List<DifferenceTable> tableerrors = Lists.newArrayList();
    private List<DifferenceIndex> indexerrors = Lists.newArrayList();
    private Integer countSourceTables=0;
    private Integer countCompareTables=0;
    
    public Integer getCountSourceTables() {
        return countSourceTables;
    }
    public void setCountSourceTables(Integer countSourceTables) {
        this.countSourceTables += countSourceTables;
    }
    public Integer getCountCompareTables() {
        return countCompareTables;
    }
    public void setCountCompareTables(Integer countCompareTables) {
        this.countCompareTables += countCompareTables;
    }
   
    public List<DifferenceTable> getTableerrors() {
        return tableerrors;
    }
    public void setTableerrors(List<DifferenceTable> tableerrors) {
        this.tableerrors = tableerrors;
    }
    public List<DifferenceIndex> getIndexerrors() {
        return indexerrors;
    }
    public void setIndexerrors(List<DifferenceIndex> indexerrors) {
        this.indexerrors = indexerrors;
    }
    
}
