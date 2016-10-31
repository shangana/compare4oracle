package compare.beans.definition;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import com.google.common.collect.Lists;

public class PDM {
    private boolean submeter = true; //是否分表
    
    private SVN svn;
    
    private Local local;
    
    public class Local {
        private List<String> paths;
        
        public List<String> getPaths() {
            return paths;
        }
        
        public void setPaths(List<String> paths) {
            this.paths = paths;
        }
    }
    
    public class SVN {
        private List<String> svnurl;
        
        private String svnuser;
        
        private String svnpwd;
        
        public List<String> getSvnurl() {
            return svnurl;
        }
        
        public void setSvnurl(String svnurl) {
            if (null == this.svnurl) {
                this.svnurl = Lists.newArrayList();
            }
            this.svnurl.add(svnurl);
        }
        
        public String getSvnuser() {
            return svnuser;
        }
        
        public void setSvnuser(String svnuser) {
            this.svnuser = svnuser;
        }
        
        public String getSvnpwd() {
            return svnpwd;
        }
        
        public void setSvnpwd(String svnpwd) {
            this.svnpwd = svnpwd;
        }
        
    }
    
    public boolean isSubmeter() {
        return submeter;
    }
    
    public void setSubmeter(boolean submeter) {
        this.submeter = submeter;
    }
    
    public SVN getSvn() {
        return svn;
    }
    
    @SuppressWarnings("unchecked")
    public void setSvn(Element element) {
        SVN svn = new SVN();
        for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext();) {
            Element item = iterator.next();
            if ("url".equalsIgnoreCase(item.getName())) {
                svn.setSvnurl(item.getText());
            }
            else if ("username".equalsIgnoreCase(item.getName())) {
                svn.setSvnuser(item.getTextTrim());
            }
            else if ("passwd".equalsIgnoreCase(item.getName())) {
                svn.setSvnpwd(item.getTextTrim());
            }
        }
        
        this.svn = svn;
    }
    
    public Local getLocal() {
        return local;
    }
    
    @SuppressWarnings("unchecked")
    public void setLocal(Element element) {
        Local local = new Local();
        List<String> paths = Lists.newArrayList();
        local.setPaths(paths);
        for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext();) {
            Element item = iterator.next();
            if ("path".equalsIgnoreCase(item.getName())) {
                if (null != item.getTextTrim() && !"".equals(item.getTextTrim())) {
                    paths.add(item.getTextTrim());
                }
            }
        }
        this.local = local;
    }
    
}
