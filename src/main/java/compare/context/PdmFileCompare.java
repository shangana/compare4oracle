package compare.context;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import compare.beans.PdmFileInfo;
import compare.beans.definition.PDM;
import compare.beans.definition.PDM.Local;
import compare.beans.definition.PDM.SVN;
import compare.core.LocalFileManger;
import compare.core.SVNFileManager;
import compare.core.XmlBeanReader;

public class PdmFileCompare {
    private static final Logger logger = LogManager.getLogger();
    
    private String checkoutRootPath;
    public static PdmFileCompare getInstance() {
        return new PdmFileCompare();
    }
    private PdmFileCompare() {
        getPdmRootPath();
    }
    
    public PdmFileInfo getPdmFileInfo(PDM pdmconfig) throws Exception {
        boolean checkout = checkout(pdmconfig);
        if (!checkout) {
            logger.error("get pdm file is error,please check pdm config!");
            throw new Exception();
        }
        return new XmlBeanReader(checkoutRootPath).getPdmFileInfo();
    }
    
    private boolean checkout(PDM pdmconfig) throws Exception {
        Local local = pdmconfig.getLocal();
        if (null != local) {
            logger.warn("---read Local config pdm file!");
            return new LocalFileManger(checkoutRootPath).checkout(local);
        }
        SVN svn = pdmconfig.getSvn();
        if (null != svn) {
            logger.warn("---read SVN config pdm file!");
            return new SVNFileManager(checkoutRootPath).checkout(svn);
        }
        return false;
    }
    
    private void getPdmRootPath() {
        File file = new File("checkout");
        //处理前先清除
        removeFile(file);
        try {
            file.mkdirs();
            checkoutRootPath = file.getCanonicalPath();
        }
        catch (IOException e) {
            e.printStackTrace();
            logger.error("获取pdm根路径出错!");
        }
    }
    
    private void removeFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File subfile : file.listFiles()) {
                    removeFile(subfile);
                }
                file.delete();
            }
            else {
                file.delete();
            }
        }
    }
}
