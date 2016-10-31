package compare.core;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import compare.beans.definition.PDM.SVN;

/**
 * @author   yueshanfei
 * @date  2016年9月19日
 */
public class SVNFileManager {
    private static final Logger logger = LogManager.getLogger();
    private String rootPath;
    
    public SVNFileManager(String rootPath) {
        this.rootPath = rootPath;
    }
    
    public boolean checkout(SVN svnconfig) {
        SVNUpdateClient client = getUpdateClient(svnconfig.getSvnuser(), svnconfig.getSvnpwd());
        List<String> svnurl = svnconfig.getSvnurl();
        try {
            if (null == rootPath) {
                return false;
            }
            int count = 0;
            for (String url : svnurl) {
                String pdmurl = url.substring(0, url.lastIndexOf(File.separator));
                String name = url.substring(url.lastIndexOf(File.separator) + 1, url.length()).trim();
                String downroot = rootPath + File.separator + (count++) + File.separator
                        + pdmurl.substring(pdmurl.lastIndexOf(File.separator) + 1, pdmurl.length());
                File downfile = new File(downroot);
                if (!downfile.exists()) {
                    downfile.mkdirs();
                }
                long checkout = client.doCheckout(SVNURL.parseURIEncoded(pdmurl),
                        downfile,
                        SVNRevision.HEAD,
                        SVNRevision.HEAD,
                        SVNDepth.EMPTY,
                        false);
                logger.debug("--version:" + checkout + " check out to path:" + downroot);
                String downpath = downroot + File.separator + name;
                long doUpdate = client.doUpdate(new File(downpath), SVNRevision.HEAD, SVNDepth.FILES, false, false);
                logger.debug("updateFile:" + downpath);
                logger.debug("PDM update to version:" + doUpdate);
            }
            return true;
        }
        catch (SVNException e) {
            logger.error(e);
            e.printStackTrace();
            return false;
        }
    }
    
    private SVNUpdateClient getUpdateClient(String user, String passwd) {
        // 初始化版本库
        setupLibrary();
        DefaultSVNOptions options = new DefaultSVNOptions();
        SVNClientManager manager = SVNClientManager.newInstance(options, user, passwd); //需要用户名密码  
        return manager.getUpdateClient();
    }
    
    /**
     * 通过不同的协议初始化版本库
     */
    private void setupLibrary() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }
}
