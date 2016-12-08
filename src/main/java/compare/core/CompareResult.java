package compare.core;

import java.util.List;

import com.google.common.collect.Lists;
import compare.context.DifferenceIndex;
import compare.context.DifferenceTable;

public class CompareResult {
    private int sourceNumber;
    private int compareNumber;
    private int diffNumber;
    private String sourceContent;
    private String compareContent;
    List<DifferenceTable> tableErrors;
    List<DifferenceIndex> indexError;
    
    public int getSourceNumber() {
        return sourceNumber;
    }
    public void setSourceNumber(int sourceNumber) {
        this.sourceNumber = sourceNumber;
    }
    public int getCompareNumber() {
        return compareNumber;
    }
    public void setCompareNumber(int compareNumber) {
        this.compareNumber = compareNumber;
    }
    
    public float getDiffrate() {
        List<String> diffTableName = Lists.newArrayList();
        for (DifferenceTable t : tableErrors) {
            if (!diffTableName.contains(t.getSourseTable())) {
                diffTableName.add(t.getSourseTable());
            }
        }
        diffNumber =diffTableName.size();
        
        return sourceNumber == 0 ? 0 : diffNumber/Float.parseFloat(sourceNumber+"");
    }
    public String getSourceContent() {
        return sourceContent;
    }
    public void setSourceContent(String sourceContent) {
        this.sourceContent = sourceContent;
    }
    public String getCompareContent() {
        return compareContent;
    }
    public void setCompareContent(String compareContent) {
        this.compareContent = compareContent;
    }
    public void setTableErrors(List<DifferenceTable> errors) {
        this.tableErrors = errors;
    }
    public int getDiffNumber() {
        return diffNumber;
    }
    public void setIndexErrors(List<DifferenceIndex> errors2) {
        this.indexError = errors2;
    }
    
}
