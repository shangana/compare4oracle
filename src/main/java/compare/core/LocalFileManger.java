package compare.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import compare.beans.definition.PDM.Local;

/**
 * @author   yueshanfei
 * @date  2016年9月19日
 */
public class LocalFileManger {
    private static final Logger logger = LogManager.getLogger();
    
    private String rootPath;
    
    public LocalFileManger(String rootPath) {
        this.rootPath = rootPath;
    }
    
    public boolean checkout(Local config) throws Exception {
        return checkout(config.getPaths());
    }
    
    private boolean checkout(List<String> localconfig) throws Exception {
        if (null == localconfig || localconfig.isEmpty()) {
            logger.error("没有配置本地pdm文件");
            return false;
        }
        try {
            if (null == rootPath) {
                return false;
            }
            int count = 0;
            for (String pdmurl : localconfig) {
                int j = pdmurl.lastIndexOf(File.separator);
                String url = pdmurl.substring(0, j);
                String name = pdmurl.substring(j + 1, pdmurl.length()).trim();
                // 获得倒数第二层目录
                int secd = url.lastIndexOf(File.separator);
                String secdpath = url.substring(secd + 1, url.length());
                String subPdmPath = rootPath + File.separator + (count++) + File.separator + secdpath;
                delDirWithFiles(subPdmPath);
                String newfile = subPdmPath + File.separator + name;
                copyFile(newfile, pdmurl);
            }
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
    }
    
    /**
     * @todo 删除文件目录
     * @param path
     *            String
     */
    public static void delDirWithFiles(String path)
    { 
        try
        {
            File dir = new File(path);
            if (dir.exists())
            {
                File[] tmp = dir.listFiles();
                for (int i = 0; i < tmp.length; i++)
                {
                    if (tmp[i].isDirectory())
                    {
                        delDirWithFiles(path + File.separator + tmp[i].getName());
                    } else
                    {
                        logger.info("del file:" + tmp[i]);
                        tmp[i].delete();

                    }
                }
                logger.info("del dir:" + dir.getAbsolutePath());
                dir.delete();
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * 
     * @param NewFile
     *            String
     * @param OldFile
     *            String
     * @throws Exception
     */
    public static void copyFile(String NewFile, String OldFile)
            throws Exception
    {
        int byteread = 0;
        File oldFile = new File(OldFile);
        if (!oldFile.exists())
        {
            throw new Exception(NewFile + ":is not exist");
        }
        if (!oldFile.isFile())
        {
            throw new Exception(NewFile + ":is not a file");
        }
        // 检查目标目录是否存在
        File fNewFile = new File(NewFile);  
        // 如果路径不存在,则创建  
        if (!fNewFile.getParentFile().exists()) {  
            fNewFile.getParentFile().mkdirs();  
        } 
       
        FileInputStream inStream = new FileInputStream(oldFile);
        FileOutputStream outStream = new FileOutputStream(NewFile);
        byte[] buf = new byte[1024];
        while ((byteread = inStream.read(buf)) != -1)
        {
            // bytesum+=byteread;
            outStream.write(buf, 0, byteread);
        }
        inStream.close();
        outStream.close();

    }
}
