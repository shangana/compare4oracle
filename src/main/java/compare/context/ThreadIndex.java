package compare.context;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import compare.beans.DatabaseInfo;
import compare.beans.Index;
import compare.beans.PdmFileInfo;

/**
 * @author   yueshanfei
 * @date  2016年10月26日
 */
public class ThreadIndex extends Thread {
    protected static final Logger logger = LogManager.getLogger();
    private String owner;
    private CountDownLatch latch;
    private List<DifferenceIndex> errors;
    private boolean single;
    private PdmFileInfo pdmFileInfo;
    private DatabaseInfo databaseInfo;
    private boolean submeter;
    
    @Override
    public void run() {
        logger.debug(owner+" 用户索引执行开始...");
        String[] split = owner.split("=");
        TreeMap<String, Index> source;
        TreeMap<String, Index> compare;
        if (single) {
            compare = Maps.newTreeMap();
            for (TreeMap<String, Index> treeMap : pdmFileInfo.getIndexs().values()) {
                compare.putAll(treeMap);
            }
            source = Maps.newTreeMap();
            for (TreeMap<String, Index> treeMap : databaseInfo.getIndexs().values()) {
                source.putAll(treeMap);
            }
        }
        else {
            source = pdmFileInfo.getIndexs(split[0]);
            compare = databaseInfo.getIndexs(split[1]);
        }
        final CountDownLatch indexlatch = new CountDownLatch(1);
        CompareIndex diff = new CompareIndex(indexlatch);
        diff.setCompare(compare);
        diff.setSource(source);
        diff.setOwner(split[1]);
        diff.setErrors(errors);
        diff.isSubmeter(submeter);
        diff.start();
        try {
            indexlatch.await();
            latch.countDown();
            logger.debug(owner+" 用户索引执行完成.");
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
    
    public List<DifferenceIndex> getErrors() {
        return errors;
    }
    
    public void setErrors(List<DifferenceIndex> errors) {
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
