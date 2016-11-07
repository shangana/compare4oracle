package compare.context;

import java.sql.Connection;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
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
    private boolean single;
    private PdmFileInfo pdmFileInfo;
    private boolean submeter;
    private TreeMap<String, Index> source;
    private TreeMap<String, Index> compare;
    private ThreadParam param;
    private OwnerParam ownerParam;
    @Override
    public void run() {
        logger.debug(owner+" The user's index execution starts...");
        
        //
        doIndexData();
        //
        if (null == source || null == compare) {
            latch.countDown();
            logger.error(new Exception(owner+" Index of this user compared to the exception."));
            return;
        }
        executeCompare();
    }

    private void executeCompare() {
        final CountDownLatch indexlatch = new CountDownLatch(1);
        CompareIndex diff = new CompareIndex(indexlatch);
        diff.setCompare(compare);
        diff.setSource(source);
        diff.setOwner(owner.split("=")[1]);
        diff.setParam(param);
        diff.setOwnerParam(ownerParam);
//        diff.setErrors(errors);
        diff.isSubmeter(submeter);
        diff.start();
        try {
            indexlatch.await();
            latch.countDown();
            logger.debug(owner+" The user's index is executed.");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doIndexData() {
        final String[] split = owner.split("=");
        final DatabaseCompare db = DatabaseCompare.getInstance();
        Thread t1 = new Thread() {
            @Override
            public void run() {
                Connection sourceConnection = ownerParam.getSourceConnection();
                if (single) {
                    if (null != pdmFileInfo) {
                        source = Maps.newTreeMap();
                        for (TreeMap<String, Index> treeMap : pdmFileInfo.getIndexs().values()) {
                            source.putAll(treeMap);
                        }
                    }
                    else if (null != sourceConnection) {
                        source = db.getIndexs(sourceConnection);
                    }
                    else
                        logger.error(new Exception("source connection get error."));
                }
                else {
                    if (null != pdmFileInfo)
                        source = pdmFileInfo.getIndexs(split[0]);
                    else if (null != sourceConnection)
                        source = db.getIndexs(sourceConnection);
                    else
                        logger.error(new Exception("source connection get error."));
                }
            }
        };
        t1.start();
        
        Thread t2 = new Thread() {
            public void run() {
                Connection databaseConnection = ownerParam.getDatabaseConnection();
                if (null != databaseConnection)
                    compare = db.getIndexs(databaseConnection);
                else
                    logger.error(new Exception("compare connection get error."));
            };
        };
        t2.start();
        
        try {
            t1.join();
            t2.join();
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
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
    
    public void setSingle(boolean single) {
        this.single = single;
    }

    public void setPdmFileInfo(PdmFileInfo pdmFileInfo) {
        this.pdmFileInfo = pdmFileInfo;
    }

    public void setSubmeter(boolean submeter) {
        this.submeter = submeter;
    }

    public void setParam(ThreadParam param) {
        this.param = param;
    }

    public void setOwnerParam(OwnerParam ownerParam) {
        this.ownerParam = ownerParam;
    }
}
