package compare.context;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import compare.beans.DatabaseInfo;
import compare.beans.PdmFileInfo;
import compare.beans.Table;

/**
 * @author   yueshanfei
 * @date  2016年10月26日
 */
public class ThreadTable extends Thread {
    protected static final Logger logger = LogManager.getLogger();
    private String owner;
    private CountDownLatch latch;
    private List<DifferenceTable> errors;
    private boolean single;
    private PdmFileInfo pdmFileInfo;
    private DatabaseInfo databaseInfo;
    private boolean submeter;
    
    @Override
    public void run() {
        logger.debug(owner+" This user's table execution starts...");
        String[] split = owner.split("=");
        TreeMap<String, Table> source;
        TreeMap<String, Table> compare;
        if (single) {
            compare = Maps.newTreeMap();
            for (TreeMap<String, Table> treeMap : pdmFileInfo.getTables().values()) {
                compare.putAll(treeMap);
            }
            source = Maps.newTreeMap();
            for (TreeMap<String, Table> treeMap : databaseInfo.getTables().values()) {
                source.putAll(treeMap);
            }
        }
        else {
            source = pdmFileInfo.getTables(split[0]);
            compare = databaseInfo.getTables(split[1]);
        }
        final CountDownLatch tablelatch = new CountDownLatch(1);
        CompareTable diff = new CompareTable(tablelatch);
        diff.setCompare(compare);
        diff.setSource(source);
        diff.setOwner(split[1]);
        diff.setErrors(errors);
        diff.isSubmeter(submeter);
        diff.start();
        try {
            tablelatch.await();
            latch.countDown();
            logger.debug(owner+" This user's table is executed.");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public CountDownLatch getLatch() {
        return latch;
    }
    
    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
    
    public List<DifferenceTable> getErrors() {
        return errors;
    }
    
    public void setErrors(List<DifferenceTable> errors) {
        this.errors = errors;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public void setPdmFileInfo(PdmFileInfo pdmFileInfo) {
        this.pdmFileInfo = pdmFileInfo;
    }

    public void setDatabaseInfo(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }

    public void setSubmeter(boolean submeter) {
        this.submeter = submeter;
    }
}
