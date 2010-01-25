/**
 * @author mchyzer
 * $Id: XmlExportGroup.java 6216 2010-01-10 04:52:30Z mchyzer $
 */
package edu.internet2.middleware.grouper.xml.export;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.CompactWriter;

import edu.internet2.middleware.grouper.attr.AttributeDefNameSet;
import edu.internet2.middleware.grouper.hibernate.AuditControl;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionType;
import edu.internet2.middleware.grouper.hibernate.HibUtils;
import edu.internet2.middleware.grouper.hibernate.HibernateHandler;
import edu.internet2.middleware.grouper.hibernate.HibernateHandlerBean;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import edu.internet2.middleware.grouper.misc.GrouperVersion;
import edu.internet2.middleware.grouper.util.GrouperUtil;


/**
 *
 */
public class XmlExportAttributeDefNameSet {

  /** uuid */
  private String uuid;
  
  /** createTime */
  private String createTime;

  /** modifierTime */
  private String modifierTime;

  /** hibernateVersionNumber */
  private long hibernateVersionNumber;

  /** contextId */
  private String contextId;

  /** depth */
  private long depth;
  
  /**
   * depth
   * @return depth
   */
  public long getDepth() {
    return this.depth;
  }

  /**
   * depth
   * @param depth1
   */
  public void setDepth(long depth1) {
    this.depth = depth1;
  }

  /** ifHasAttributeDefNameId */
  private String ifHasAttributeDefNameId;
  
  /**
   * ifHasAttributeDefNameId
   * @return ifHasAttributeDefNameId
   */
  public String getIfHasAttributeDefNameId() {
    return this.ifHasAttributeDefNameId;
  }

  /**
   * ifHasAttributeDefNameId
   * @param ifHasAttributeDefNameId1
   */
  public void setIfHasAttributeDefNameId(String ifHasAttributeDefNameId1) {
    this.ifHasAttributeDefNameId = ifHasAttributeDefNameId1;
  }

  /**
   * thenHasAttributeDefNameId
   */
  private String thenHasAttributeDefNameId;
  
  /**
   * thenHasAttributeDefNameId
   * @return thenHasAttributeDefNameId
   */
  public String getThenHasAttributeDefNameId() {
    return this.thenHasAttributeDefNameId;
  }

  /**
   * thenHasAttributeDefNameId
   * @param thenHasAttributeDefNameId1
   */
  public void setThenHasAttributeDefNameId(String thenHasAttributeDefNameId1) {
    this.thenHasAttributeDefNameId = thenHasAttributeDefNameId1;
  }

  /**
   * parentAttributeAssignActionSetId
   */
  private String parentAttributeDefNameSetId;
  
  /**
   * parentAttributeAssignActionSetId
   * @return parentAttributeAssignActionSetId
   */
  public String getParentAttributeDefNameSetId() {
    return this.parentAttributeDefNameSetId;
  }

  /**
   * parentAttributeAssignActionSetId
   * @param parentAttributeDefNameSetId1
   */
  public void setParentAttributeDefNameSetId(String parentAttributeDefNameSetId1) {
    this.parentAttributeDefNameSetId = parentAttributeDefNameSetId1;
  }

  /**
   * type
   */
  private String type;
  
  /**
   * type
   * @return type
   */
  public String getType() {
    return this.type;
  }

  /**
   * type
   * @param type1
   */
  public void setType(String type1) {
    this.type = type1;
  }

  /**
   * 
   */
  public XmlExportAttributeDefNameSet() {
    
  }

  /**
   * @param attributeDefNameSet
   * @param grouperVersion
   */
  public XmlExportAttributeDefNameSet(GrouperVersion grouperVersion, AttributeDefNameSet attributeDefNameSet) {
    
    if (attributeDefNameSet == null) {
      throw new RuntimeException();
    }
    
    if (grouperVersion == null) {
      throw new RuntimeException();
    }
    
    this.contextId = attributeDefNameSet.getContextId();
    this.createTime = GrouperUtil.dateStringValue(attributeDefNameSet.getCreatedOnDb());
    this.depth = attributeDefNameSet.getDepth();
    this.hibernateVersionNumber = attributeDefNameSet.getHibernateVersionNumber();
    this.ifHasAttributeDefNameId = attributeDefNameSet.getIfHasAttributeDefNameId();
    this.modifierTime = GrouperUtil.dateStringValue(attributeDefNameSet.getLastUpdatedDb());
    this.parentAttributeDefNameSetId = attributeDefNameSet.getParentAttrDefNameSetId();
    this.thenHasAttributeDefNameId = attributeDefNameSet.getThenHasAttributeDefNameId();
    this.type = attributeDefNameSet.getTypeDb();
    this.uuid = attributeDefNameSet.getId();
    
  }

  /**
   * uuid
   * @return uuid
   */
  public String getUuid() {
    return this.uuid;
  }

  /**
   * uuid
   * @param uuid1
   */
  public void setUuid(String uuid1) {
    this.uuid = uuid1;
  }

  /**
   * createTime
   * @return createTime
   */
  public String getCreateTime() {
    return this.createTime;
  }

  /**
   * createTime
   * @param createTime1
   */
  public void setCreateTime(String createTime1) {
    this.createTime = createTime1;
  }

  /**
   * modifierTime
   * @return modifierTime
   */
  public String getModifierTime() {
    return this.modifierTime;
  }

  /**
   * modifierTime
   * @param modifierTime1
   */
  public void setModifierTime(String modifierTime1) {
    this.modifierTime = modifierTime1;
  }

  /**
   * hibernateVersionNumber
   * @return hibernateVersionNumber
   */
  public long getHibernateVersionNumber() {
    return this.hibernateVersionNumber;
  }

  /**
   * hibernateVersionNumber
   * @param hibernateVersionNumber1
   */
  public void setHibernateVersionNumber(long hibernateVersionNumber1) {
    this.hibernateVersionNumber = hibernateVersionNumber1;
  }

  /**
   * contextId
   * @return contextId
   */
  public String getContextId() {
    return this.contextId;
  }

  /**
   * contextId
   * @param contextId1
   */
  public void setContextId(String contextId1) {
    this.contextId = contextId1;
  }
  
  /**
   * convert to attributeDefNameSet
   * @return the attributeDefNameSet
   */
  public AttributeDefNameSet toAttributeDefNameSet() {
    AttributeDefNameSet attributeDefNameSet = new AttributeDefNameSet();
    
    attributeDefNameSet.setContextId(this.contextId);
    attributeDefNameSet.setCreatedOnDb(GrouperUtil.dateLongValue(this.createTime));
    attributeDefNameSet.setDepth((int)this.depth);
    attributeDefNameSet.setHibernateVersionNumber(this.hibernateVersionNumber);
    attributeDefNameSet.setIfHasAttributeDefNameId(this.ifHasAttributeDefNameId);
    attributeDefNameSet.setLastUpdatedDb(GrouperUtil.dateLongValue(this.modifierTime));
    attributeDefNameSet.setParentAttrDefNameSetId(this.parentAttributeDefNameSetId);
    attributeDefNameSet.setThenHasAttributeDefNameId(this.thenHasAttributeDefNameId);
    attributeDefNameSet.setTypeDb(this.type);
    attributeDefNameSet.setId(this.uuid);
    
    return attributeDefNameSet;
  }

  /**
   * @param exportVersion
   * @return the xml string
   */
  public String toXml(GrouperVersion exportVersion) {
    StringWriter stringWriter = new StringWriter();
    this.toXml(exportVersion, stringWriter);
    return stringWriter.toString();
  }

  /**
   * @param exportVersion 
   * @param writer
   */
  public void toXml(
      @SuppressWarnings("unused") GrouperVersion exportVersion, Writer writer) {
    XStream xStream = XmlExportUtils.xstream();
  
    CompactWriter compactWriter = new CompactWriter(writer);
    
    xStream.marshal(this, compactWriter);
  
  }

  /**
   * 
   * @param writer
   * @param xmlExportMain
   */
  public static void exportAttributeDefNameSets(final Writer writer, final XmlExportMain xmlExportMain) {
    //get the members
    HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_OR_USE_EXISTING, AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {
      
      public Object callback(HibernateHandlerBean hibernateHandlerBean)
          throws GrouperDAOException {
  
        Session session = hibernateHandlerBean.getHibernateSession().getSession();
  
        //select all action sets (immediate is depth = 1)
        Query query = session.createQuery(
            "select theAttributeDefNameSet from AttributeDefNameSet as theAttributeDefNameSet where theAttributeDefNameSet.typeDb = 'immediate' order by theAttributeDefNameSet.id");
  
        GrouperVersion grouperVersion = new GrouperVersion(GrouperVersion.GROUPER_VERSION);
        try {
          writer.write("  <attributeDefNameSets>\n");
  
          //this is an efficient low-memory way to iterate through a resultset
          ScrollableResults results = null;
          try {
            results = query.scroll();
            while(results.next()) {
              Object object = results.get(0);
              final AttributeDefNameSet attributeDefNameSet = (AttributeDefNameSet)object;
              
              //comments to dereference the foreign keys
              if (xmlExportMain.isIncludeComments()) {
                HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_NEW, AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {
                  
                  public Object callback(HibernateHandlerBean hibernateHandlerBean)
                      throws GrouperDAOException {
                    try {
                      writer.write("\n    <!-- ");

                      XmlExportUtils.toStringAttributeDefName("ifHas", writer, attributeDefNameSet.getIfHasAttributeDefNameId(), true);
                      XmlExportUtils.toStringAttributeDefName("thenHas", writer, attributeDefNameSet.getThenHasAttributeDefNameId(), false);

                      writer.write(" -->\n");
                      return null;
                    } catch (IOException ioe) {
                      throw new RuntimeException(ioe);
                    }
                  }
                });
              }
              
              XmlExportAttributeDefNameSet xmlExportAttributeDefNameSet = new XmlExportAttributeDefNameSet(grouperVersion, attributeDefNameSet);
              writer.write("    ");
              xmlExportAttributeDefNameSet.toXml(grouperVersion, writer);
              writer.write("\n");
            }
          } finally {
            HibUtils.closeQuietly(results);
          }
          
          if (xmlExportMain.isIncludeComments()) {
            writer.write("\n");
          }
          
          //end the attribute def name sets element 
          writer.write("  </attributeDefNameSets>\n");
        } catch (IOException ioe) {
          throw new RuntimeException("Problem with streaming attributeDefNameSets", ioe);
        }
        return null;
      }
    });
  }

  /**
   * take a reader (e.g. dom reader) and convert to an xml export group
   * @param exportVersion
   * @param hierarchicalStreamReader
   * @return the bean
   */
  public static XmlExportAttributeDefNameSet fromXml(@SuppressWarnings("unused") GrouperVersion exportVersion, 
      HierarchicalStreamReader hierarchicalStreamReader) {
    XStream xStream = XmlExportUtils.xstream();
    
    XmlExportAttributeDefNameSet xmlExportAttributeDefNameSet = (XmlExportAttributeDefNameSet)xStream.unmarshal(hierarchicalStreamReader);
  
    return xmlExportAttributeDefNameSet;
  }

  /**
   * 
   * @param exportVersion
   * @param xml
   * @return the object from xml
   */
  public static XmlExportAttributeDefNameSet fromXml(
      @SuppressWarnings("unused") GrouperVersion exportVersion, String xml) {
    XStream xStream = XmlExportUtils.xstream();
    
    XmlExportAttributeDefNameSet xmlExportAttributeDefNameSet = (XmlExportAttributeDefNameSet)xStream.fromXML(xml);
  
    return xmlExportAttributeDefNameSet;
  }

}