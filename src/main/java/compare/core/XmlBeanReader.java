package compare.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import compare.beans.PdmFileInfo;
import compare.beans.TabColumn;
import compare.beans.XMLIndex;
import compare.beans.XMLKey;
import compare.beans.XMLTable;

/**
 * @author   yueshanfei
 * @date  2016年9月19日
 */
public class XmlBeanReader {
    
    private static final Logger logger = LogManager.getLogger();
    
    private String rootPath;
    
    public XmlBeanReader(String rootPath) {
        this.rootPath = rootPath;
    }
    
    public PdmFileInfo getPdmFileInfo() {
        LinkedHashMap<String, Document> documents = getDocuments();
        PdmFileInfo info = new PdmFileInfo();
        Iterator<Entry<String, Document>> iterator = documents.entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<String, Document> entry = iterator.next();
            String key = entry.getKey(); //pdm file path
            logger.debug("--dealing pdm file ="+key);
            Document document = entry.getValue();
            List<XMLTable> tables = getTables(key, document);
            for (XMLTable xmlTable : tables) {
                info.addTables(xmlTable.getUsers(), xmlTable.toTable());
                info.addIndexs(xmlTable.getUsers(), xmlTable.toIndex());
                info.setUsers(xmlTable.getUsers());
            }
        }
        return info;
    }
    
    @SuppressWarnings("unchecked")
    private List<XMLTable> getTables(String pdmfilepath, Document document) {
        
        Map<String, String> userMap = getUsers(document);
        List<XMLTable> tables = Lists.newArrayList();
        Map<String, String> map = new HashMap<String, String>();
        map.put("c", "collection");
        // 根据xml文档，//c:Table 即为得到的文档对象
        XPath path = document.createXPath("//c:Tables");
        path.setNamespaceURIs(map);
        List<Element> list = path.selectNodes(document);
        for (Element element : list) {
            Iterator<Element> iterator = element.elementIterator("Table");
            while (iterator.hasNext()) {
                XMLTable table = new XMLTable();
                tables.add(table);
                table.setPdmfile(pdmfilepath);
                Element tableElement = iterator.next();
                String tableCode = tableElement.elementText("Code");
                table.setCode(tableCode);
                //获取列
                TreeMap<String, TabColumn> columns = getColumns(tableElement);
                table.setColumns(columns);
                //获取列的id与列名的引用
                Map<String, String> refIdMap = Maps.newHashMap();
                Iterator<Entry<String, TabColumn>> iterator2 = columns.entrySet().iterator();
                while(iterator2.hasNext()) {
                    Entry<String, TabColumn> next = iterator2.next();
                    String id = next.getKey();
                    TabColumn column = next.getValue();
                    refIdMap.put(id, column.getColumnName());
                }
                table.setRefIdMap(refIdMap);
                //Keys
                TreeMap<String, XMLKey> keys = getKeys(tableElement);
                table.setKeys(keys);
                //primaryKey
                String primaryKey = getPrimaryKey(tableElement);
                table.setPrimaryKey(primaryKey);
                //Indexs
                List<XMLIndex> indexs = getIndexs(tableElement);
                table.setIndexs(indexs);
                //Owner
                String ownerRefId = getOwner(tableElement);
                String owner = userMap.get(ownerRefId);
                List<String> users = Lists.newArrayList();
                if (null != owner && owner.contains("+")) {
                    String[] owners = owner.split("\\+");
                    for (int i = 0; i < owners.length; i++) {
                        if (owners[i] != null && owners[i] != "") {
                            if (!users.contains(owners[i])) {
                                users.add(owners[i]);
                            }
                        }
                    }
                } else {
                    users.add(owner);
                }
                table.setUsers(users);
            }
        }
        return tables;
    }
    
    private String getOwner(Element tableElement) {
        Element ownerElement = tableElement.element("Owner");
        if (ownerElement != null) {
            Element userElement = ownerElement.element("User");
            if (userElement != null) {
                return userElement.attribute("Ref").getText();
            }
        }
        return null;
    }
    
    private String getPrimaryKey(Element tableElement) {
        Element primaryKey = tableElement.element("PrimaryKey");
        if (primaryKey != null) {
            Element primaryColumn = primaryKey.element("Key");
            if (primaryColumn != null) {
                return primaryColumn.attribute("Ref").getText();
            }
        }
        logger.debug("table not is PrimaryKey, table_name="+tableElement.element("Code").getText());
        return null;
    }
    @SuppressWarnings("unchecked")
    private TreeMap<String,XMLKey> getKeys(Element tableElement) {
        
        String primaryKeyId = getPrimaryKey(tableElement);
        TreeMap<String,XMLKey> keyMap = Maps.newTreeMap();
        Element keysElement = tableElement.element("Keys");
        if (null == keysElement) {
            return null;
        }
        Iterator<Element> iterator = keysElement.elementIterator("Key");
        while(iterator.hasNext()) {
            Element keyElement = iterator.next();
            XMLKey xmlkey = new XMLKey();
            String id = keyElement.attribute("Id").getText().trim();
            keyMap.put(id, xmlkey);
            xmlkey.setId(id);
            String constraintName = keyElement.elementText("ConstraintName");
            if (null == constraintName) {
                //如果是主键,则默认PK 否则,默认AK
                if (null != primaryKeyId && primaryKeyId.equals(id)) {
                    constraintName = "PK_"+tableElement.elementText("Code");
                } else {
                    constraintName = "AK_"+tableElement.elementText("Code");
                }
            }
            if (constraintName.length()>31) {
                constraintName = constraintName.substring(0, 31);
            }
            xmlkey.setConstraintName(constraintName.replaceAll("\\s*", ""));
            Element keyColumn = keyElement.element("Key.Columns");
            if (keyColumn != null) {
                List<String> refIds = Lists.newArrayList();
                xmlkey.setColumnRefIds(refIds);
                Iterator<Element> iterator2 = keyColumn.elementIterator("Column");
                while (iterator2.hasNext()) {
                    Element columnElement = iterator2.next();
                    Attribute attribute = columnElement.attribute("Ref");
                    refIds.add(attribute.getText());
                }
            }
        }
        return keyMap;
    }
    @SuppressWarnings("unchecked")
    private List<XMLIndex> getIndexs(Element tableElement) {
        List<XMLIndex> indexs = Lists.newArrayList();
        Element indexsElement = tableElement.element("Indexes");
        if (null == indexsElement) {
            return null;
        }
        Iterator<Element> iterator = indexsElement.elementIterator("Index");
        while(iterator.hasNext()) {
            Element indexElement = iterator.next();
            String code = indexElement.element("Code").getText();
            //LinkedObject Key  是主键
            Element linkedObjectElement = indexElement.element("LinkedObject");
logger.debug("--index code="+code);
            XMLIndex index = new XMLIndex();
            index.setCode(code);
            if (null != linkedObjectElement) {
                Element keyElement = linkedObjectElement.element("Key");
                if (null != keyElement) {
                    continue;
                }
                //表示: 外键
                Element referenceElement = linkedObjectElement.element("Reference");
                index.setLinkedObjectReferenceRefId(referenceElement.attributeValue("Ref"));
                
            }
          //除主键外,存在 Unique 为唯一索引
            indexs.add(index);
            Element uniqueElement =indexElement.element("Unique");
            if (null != uniqueElement) {
                index.setUnique("UNIQUE");
            }
            //
            Element indexColumnElement = indexElement.element("IndexColumns");
            if (indexColumnElement != null) {
                List<String> columnRefIds = Lists.newArrayList();
                index.setIndexColumnRefIds(columnRefIds);
                Iterator<Element> elementIterator = indexColumnElement.elementIterator("IndexColumn");
                while(elementIterator.hasNext()) {
                    Element next = elementIterator.next();
                    Element columnElement = next.element("Column");
                    if (null != columnElement) {
                        Attribute attribute = columnElement.element("Column").attribute("Ref");
                        columnRefIds.add(attribute.getText());
                    } else {
                        Element expression = next.element("IndexColumn.Expression");
                        if (null != expression) {
                            columnRefIds.add(expression.getText());
                        }
                    }
                }
            }
            
        }
        return indexs;
    }
    
    
    /** <id,column>
     * @param tableElement
     * @return
     */
    @SuppressWarnings("unchecked")
    private TreeMap<String,TabColumn> getColumns(Element tableElement) {
        Element columns = tableElement.element("Columns");
        Iterator<Element> iterator = columns.elementIterator("Column");
        TreeMap<String,TabColumn> columnMap = Maps.newTreeMap();
        int index = 1;
        while (iterator.hasNext()) {
            TabColumn xmlcolumn = new TabColumn();
            Element columnElement = iterator.next();
            String id = columnElement.attribute("Id").getText();
            String code = columnElement.elementText("Code");
            columnMap.put(id, xmlcolumn);
            xmlcolumn.setColumnName(code);
            xmlcolumn.setColumnId(index++ +"");
            String elementText = columnElement.elementText("DataType");
            String dataType = null != elementText ? elementText.trim():elementText;
            xmlcolumn.setColumnType(dataType);
            String isnull = columnElement.elementText("Mandatory");
            String iscolumnnull = columnElement.elementText("Column.Mandatory");
            if (null != isnull || null != iscolumnnull) {
                xmlcolumn.setNullable("N");
            }
            else {
                xmlcolumn.setNullable("Y");
            }
            String defaultValue = columnElement.elementText("DefaultValue");
            xmlcolumn.setDataDefault(defaultValue);
        }
        return columnMap;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, String> getUsers(Document document) {
        Map<String, String> result =Maps.newHashMap();
        // 根据xpath方式得到所要得到xml文档的具体对象,根据分析解析xml文档可知，xml文档中含有前缀名
        Map<String, String> map = Maps.newHashMap();
        map.put("c", "collection");
        // 根据xml文档，//c:Table 即为得到的文档对象
        XPath path = document.createXPath("//c:Users");
        path.setNamespaceURIs(map);
        List<Element> list = path.selectNodes(document);
        // 得到tables对象，该对象是该pdm文件中所有表的集合
        for (Element element : list) {
            for (Iterator<Element> iter = element.elementIterator("User"); iter.hasNext();) {
                Element userElement = iter.next();
                Attribute attribute = userElement.attribute("Id");
                result.put(attribute.getText(), userElement.elementText("Code"));
            }
        }
        return result;
        
    }
    
    private LinkedHashMap<String, Document> getDocuments() {
        List<File> pdmfiles = getPdmFile();
        LinkedHashMap<String, Document> files = Maps.newLinkedHashMap();
        for (File file : pdmfiles) {
            SAXReader reader = new SAXReader();
            String xml = file.getAbsoluteFile() + ".tmp.xml";
            File xmlFile = toXMLFile(file, xml);
            if (null == xmlFile) {
                logger.error("pdm文件转成xml文件失败! -- " + xml);
                continue;
            }
            try {
                Document document = reader.read(xmlFile);
                files.put(file.getName(), document);
            }
            catch (DocumentException e) {
                e.printStackTrace();
            }
            
        }
        return files;
    }
    
    private File toXMLFile(File file, String xml) {
        String line;
        File xmlfile = new File(xml);
        if (xmlfile.exists()) {
            xmlfile.delete();
        }
        String charsetName = MailHandler.getCharsetName(file);
        FileOutputStream out = null;
        BufferedReader br = null;
        try {
            
//            xmlfile.createNewFile();
            out = new FileOutputStream(xmlfile, true);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
                out.write(line.getBytes(charsetName));
                out.write("\r\n".getBytes());// 写入一个换行  
            }
            return xmlfile;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (null != br) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != out) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    private List<File> getPdmFile() {
        File file = new File(rootPath);
        List<File> pdmfiles = Lists.newArrayList();
        readFile(file, pdmfiles);
        return pdmfiles;
    }
    
    private void readFile(File file, List<File> pdmfiles) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                readFile(file2, pdmfiles);
            }
        }
        else if (file.isFile() && file.getName().toLowerCase().endsWith(".pdm")) {
            pdmfiles.add(file);
        }
    }

   
}
